package com.huawei.util;

import com.huawei.entity.*;
import com.huawei.graph.util.Path;

import java.io.IOException;
import java.util.*;

/**
 * @author 郭世明
 * @date 2019/3/17 14:56
 */
public class ScheduleSim {

    private static final int IS_DUPLEX = 1;

    private static final int MAX_TIMES = 10000;

    //private static final Map<String, Road> ROAD_MAP = new HashMap<>(100);

    private static final Map<String, Car> CAR_MAP = new HashMap<>(10000);

    private static final Map<String, CarOnRoad> CAR_ON_ROAD_MAP = new HashMap<>(10000);

    private static final Map<String, Cross> CROSS_MAP = new HashMap<>(60);

    private static final Map<String, RoadCondition> ROAD_CONDITION_MAP = new HashMap<>(100);

    private static final int DEAD_LOCK = -1;

    private static int lock = 0;

    private static int TIMER = 0;

    private static int change_waiting_car = 0;

    //初始化
    public void initMap(String carPath, String crossPath, String roadPath) throws IOException {
        ReadUtil readUtil = new ReadUtil();
        readUtil.initCarMap(CAR_MAP, carPath);
        readUtil.initCrossMap(CROSS_MAP, crossPath);
        readUtil.initRoadCondition(ROAD_CONDITION_MAP, roadPath);
    }

    //调度开始
    public int runSchedule(List<Path> paths) {
        ReadUtil readUtil = new ReadUtil();
        readUtil.initCarOnRoad(CAR_ON_ROAD_MAP, CAR_MAP, paths);
        return startSchedule();
    }

    public int startSchedule() {
        TIMER = 1;
        for (; TIMER < MAX_TIMES; TIMER++) {
            Set<String> set = ROAD_CONDITION_MAP.keySet();
            Object[] arr = set.toArray();
            Arrays.sort(arr, new StringNumComparator());
            for (Object key : arr) {
                markCars(ROAD_CONDITION_MAP.get(key));
            }
            lock = 0;
            change_waiting_car = 0;
            while (hasWaitingCarInRoad()) {
                lock = 0;
                change_waiting_car = 0;
                Set<String> set2 = CROSS_MAP.keySet();
                Object[] arr2 = set2.toArray();
                Arrays.sort(arr2, new StringNumComparator());
                for (Object key : arr2) {
                    Cross cross = CROSS_MAP.get(key);
                    List<String> roads = getOrderedRoads(cross);
                    for (String road : roads) {
                        scheduleOneRoadCars(ROAD_CONDITION_MAP.get(road), cross.getId());
                    }
                }
                //System.out.print("本次调度stop：");
                //System.out.println(lock);
                if (lock == 0 && change_waiting_car == 0) {//死锁
                    return DEAD_LOCK;
                }
            }
            if (TIMER % 100 == 0)
                System.out.println(TIMER);
            if (!hasCarNotArrived()) {
                break;
            }
            driveCarInGarage();
        }
        return TIMER;
    }

    private boolean hasCarInGarbage() {
        Set<String> set = CAR_ON_ROAD_MAP.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr, new StringNumComparator());
        for (Object key : arr) {
            if (CAR_ON_ROAD_MAP.get(key).getCarStatus().equals(CarStatus.NOTSTART))
                return true;
        }
        return false;
    }

    private boolean hasCarNotArrived() {
        Set<String> set = CAR_ON_ROAD_MAP.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr, new StringNumComparator());
        for (Object key : arr) {
            if (!CAR_ON_ROAD_MAP.get(key).getCarStatus().equals(CarStatus.ARRIVED))
                return true;
        }
        return false;
    }

    private List<String> getOrderedRoads(Cross cross) {
        List<String> roads = new ArrayList<>();
        if (!cross.getRoadId1().equals("-1"))
            roads.add(cross.getRoadId1());
        if (!cross.getRoadId2().equals("-1"))
            roads.add(cross.getRoadId2());
        if (!cross.getRoadId3().equals("-1"))
            roads.add(cross.getRoadId3());
        if (!cross.getRoadId4().equals("-1"))
            roads.add(cross.getRoadId4());
        roads.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });
        return roads;
    }


    /**
     * 调度某一道路某个方向车辆
     *
     * @param roadCondition
     * @param crossId
     */
    public void scheduleOneRoadCars(RoadCondition roadCondition, String crossId) {

        if (!hasWaitingCarOnRoad(roadCondition, crossId)) {
            return;
        }

        while (hasWaitingCarInRoad(roadCondition, crossId)) {
            List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition, crossId);
            CarOnRoad carOnRoad = getFirstPriorityCar(channels);
            if (isConflict(carOnRoad, crossId)) {
                break;
            }
            Channel channel = carOnRoad.getChannel();

            boolean is_moved = moveToNextRoad(roadCondition, channel, carOnRoad, crossId);
            if (!is_moved) {
                break;
            }

        }
    }

    /**
     * 还有等待车辆
     *
     * @return
     */
    private boolean hasWaitingCarInRoad() {
        Set<String> set = CAR_ON_ROAD_MAP.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr, new StringNumComparator());
        for (Object key : arr) {
            if (CAR_ON_ROAD_MAP.get(key).getCarStatus().equals(CarStatus.WAIT))
                return true;
        }
        return false;
    }

    private boolean hasWaitingCarInRoad(RoadCondition roadCondition, String cross) {
        List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition, cross);
        for (Channel channel : channels) {
            if (channel.getCars().size() != 0)
                if (channel.getCars().get(0).getCarStatus().equals(CarStatus.WAIT))
                    return true;
        }
        return false;
    }

    /**
     * 车库里面的车出发
     */
    private void driveCarInGarage() {
        Set<String> set = CAR_ON_ROAD_MAP.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr, new StringNumComparator());
        List<CarOnRoad> carOnRoads = new ArrayList<>();
        for (Object key : arr) {
            CarOnRoad carOnRoad = CAR_ON_ROAD_MAP.get(key);
            if (carOnRoad.getCarStatus().equals(CarStatus.NOTSTART) && carOnRoad.getStartTime() <= TIMER) {
                carOnRoads.add(carOnRoad);
            }
        }
        carOnRoads.sort(new Comparator<CarOnRoad>() {
            @Override
            public int compare(CarOnRoad o1, CarOnRoad o2) {
                if (o1.getStartTime() == o2.getStartTime())
                    return Integer.parseInt(o1.getCar().getId()) - Integer.parseInt(o2.getCar().getId());
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        System.out.print("本次发车：");
        System.out.println(carOnRoads.size());
        for (CarOnRoad carOnRoad : carOnRoads) {
            String nextRoad = carOnRoad.getPath().peek();
            RoadCondition road2Condition = ROAD_CONDITION_MAP.get(nextRoad);
            int v = Math.min(carOnRoad.getCar().getSpeed(), road2Condition.getRoad().getSpeed());
            List<Channel> nextChannels = getChannelsFromRoadOnOneDirection(nextRoad, carOnRoad.getCar().getFrom());
            int ch_no = 0;
            for (Channel ch : nextChannels) {
                if (ch.getCars().size() == 0) {//目的车道没车
                    change_waiting_car++;
                    carOnRoad.setCarStatus(CarStatus.STOP);
                    carOnRoad.setRoadId(nextRoad);
                    carOnRoad.getPath().poll();
                    carOnRoad.setPosition(v);
                    ch.getCars().add(carOnRoad);
                    break;
                }
                if (ch.getCars().size() > 0) {
                    CarOnRoad con = ch.getCars().get(ch.getCars().size() - 1);//最后一辆车
                    if (con.getPosition() == 1) {//如果在车道最后，继续下一车道
                        ch_no++;
                        if (ch_no == nextChannels.size()) {
                            break;
                        }
                        continue;
                    }
                    if (con.getPosition() <= v) {//被车挡住
                        carOnRoad.setPosition(con.getPosition() - 1);
                    } else {
                        carOnRoad.setPosition(v);
                    }
                    carOnRoad.setCarStatus(CarStatus.STOP);
                    change_waiting_car++;
                    carOnRoad.setRoadId(nextRoad);
                    carOnRoad.getPath().poll();
                    ch.getCars().add(carOnRoad);
                    break;
                }
            }
        }
    }

    private boolean moveToNextRoad(RoadCondition roadCondition, Channel channel, CarOnRoad carOnRoad, String crossId) {
        //到达目的地
        if (carOnRoad.getPath().size() == 0) {
            carOnRoad.setCarStatus(CarStatus.ARRIVED);
            lock++;
            channel.getCars().remove(carOnRoad);
            //driveAllCarJustOnRoadToEndStateOnSingleChannel(channel);//标记其他车
            driveRestAllCarJustOnRoadToEndStateOnSingleChannel(channel);

            return true;
        }
        //观察目的道路路的车位情况
        String nextRoad = carOnRoad.getPath().peek();
        RoadCondition road2Condition = ROAD_CONDITION_MAP.get(nextRoad);
        List<Channel> nextChannels = getChannelsFromRoadOnOneDirection(nextRoad, crossId);
        int s1 = roadCondition.getRoad().getLength() - carOnRoad.getPosition();
        int v2 = Math.min(road2Condition.getRoad().getSpeed(), carOnRoad.getCar().getSpeed());

        if (s1 >= v2) {//还不能通过路口

            int prePosition = carOnRoad.getPosition();
            carOnRoad.setPosition(roadCondition.getRoad().getLength());
            carOnRoad.setCarStatus(CarStatus.STOP);
            change_waiting_car++;
            if (carOnRoad.getPosition() != prePosition) {
                lock++;
            }
            driveRestCarJustOnRoadToEndStateOnSingleChannel(channel);//标记调度后面车
            return true;
        } else {
            int ch_no = 0;
            for (Channel ch : nextChannels) {

                if (ch.getCars().size() == 0) {//目的车道没车
                    change_waiting_car++;
                    carOnRoad.setCarStatus(CarStatus.STOP);
                    lock++;
                    channel.getCars().remove(carOnRoad);
                    carOnRoad.setRoadId(nextRoad);
                    carOnRoad.getPath().poll();
                    carOnRoad.setPosition(v2 - s1);
                    ch.getCars().add(carOnRoad);
                    driveRestAllCarJustOnRoadToEndStateOnSingleChannel(channel);
                    return true;
                }

                if (ch.getCars().size() > 0) {
                    CarOnRoad con = ch.getCars().get(ch.getCars().size() - 1);//最后一辆车
                    if (con.getPosition() == 1) {//如果在车道最后，继续下一车道
                        if (con.getCarStatus().equals(CarStatus.WAIT)) {
                            return false;
                        }
                        ch_no++;
                        if (ch_no == nextChannels.size()) {
                            change_waiting_car++;
                            carOnRoad.setCarStatus(CarStatus.STOP);
                            int prePosition = carOnRoad.getPosition();
                            carOnRoad.setPosition(roadCondition.getRoad().getLength());
                            driveRestCarJustOnRoadToEndStateOnSingleChannel(channel);
                            if (carOnRoad.getPosition() != prePosition) {
                                lock++;
                            }
                            return true;
                        }
                        continue;
                    }
                    if (con.getPosition() <= v2 - s1) {//被车挡住
                        if (con.getCarStatus().equals(CarStatus.WAIT)) {//挡住的车是wait状态
                            return false;
                        }
                        carOnRoad.setPosition(con.getPosition() - 1);
                    } else {
                        carOnRoad.setPosition(v2 - s1);
                    }
                    change_waiting_car++;
                    carOnRoad.setCarStatus(CarStatus.STOP);
                    lock++;
                    carOnRoad.setRoadId(nextRoad);
                    carOnRoad.getPath().poll();
                    channel.getCars().remove(carOnRoad);
                    ch.getCars().add(carOnRoad);
                    //driveAllCarJustOnRoadToEndStateOnSingleChannel(channel);//标记其他车
                    driveRestAllCarJustOnRoadToEndStateOnSingleChannel(channel);
                    return true;
                }

            }
        }
        return false;
    }


    private boolean isConflict(CarOnRoad car, String crossId) {
        if (car.getPath().size() == 0)//到终点
            return false;
        Cross cross = CROSS_MAP.get(crossId);
        TurnInfo turnInfo = turnDirection(cross, car);//转向冲突车道信息
        return checkConflict(turnInfo, cross);
    }

    /**
     * 检查有各个方向没有冲突
     *
     * @param turnInfo
     * @param cross
     * @return
     */
    private boolean checkConflict(TurnInfo turnInfo, Cross cross) {
        if (turnInfo.getTurnDirection().equals(TurnDirection.STRAIGHT))
            return false;
        if (turnInfo.getTurnDirection().equals(TurnDirection.LFFT)) {
            String road = turnInfo.getRoads().get(0);
            CarOnRoad conflictCar = getConflictCar(road, cross.getId());
            if (conflictCar == null) {
                return false;
            }
            return turnDirection(cross, conflictCar).getTurnDirection().equals(TurnDirection.STRAIGHT);
        }
        if (turnInfo.getTurnDirection().equals(TurnDirection.RIGHT)) {
            String sRoad = turnInfo.getRoads().get(0);
            CarOnRoad sConflictCar = getConflictCar(sRoad, cross.getId());
            String lRoad = turnInfo.getRoads().get(1);
            CarOnRoad lConflictCar = getConflictCar(lRoad, cross.getId());
            if (sConflictCar == null && lConflictCar == null) {
                return false;
            }
            if (sConflictCar == null) {
                return turnDirection(cross, lConflictCar).getTurnDirection().equals(TurnDirection.LFFT);
            }
            if (lConflictCar == null) {
                return turnDirection(cross, sConflictCar).getTurnDirection().equals(TurnDirection.STRAIGHT);
            }
            return turnDirection(cross, sConflictCar).getTurnDirection().equals(TurnDirection.STRAIGHT)
                    || turnDirection(cross, lConflictCar).getTurnDirection().equals(TurnDirection.LFFT);
        }
        return false;
    }

    /**
     * 得到某一道路某方向第一优先级车
     *
     * @param roadId
     * @param crossId
     * @return
     */
    private CarOnRoad getConflictCar(String roadId, String crossId) {
        RoadCondition roadCondition = ROAD_CONDITION_MAP.get(roadId);
        if (roadCondition == null) {
            return null;
        }
        List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition, crossId);
        return getFirstPriorityCar(channels);
    }

    /**
     * 转向信息
     *
     * @param cross
     * @return 方向以及冲突车道
     */
    private TurnInfo turnDirection(Cross cross, CarOnRoad car) {
        String nowRoad = car.getRoadId();
        if (car.getPath().size() == 0)//到达终点直接设置直行
            return new TurnInfo(TurnDirection.STRAIGHT);
        String nextRoad = car.getPath().peek();
        TurnInfo turnInfo = new TurnInfo();
        int now = 0, next = 0;
        if (!cross.getRoadId1().equals("-1")) {
            if (cross.getRoadId1().equals(nowRoad))
                now = 0;
            else if (cross.getRoadId1().equals(nextRoad))
                next = 0;
        }
        if (!cross.getRoadId2().equals("-1")) {
            if (cross.getRoadId2().equals(nowRoad))
                now = 1;
            if (cross.getRoadId2().equals(nextRoad))
                next = 1;
        }
        if (!cross.getRoadId3().equals("-1")) {
            if (cross.getRoadId3().equals(nowRoad))
                now = 2;
            if (cross.getRoadId3().equals(nextRoad))
                next = 2;
        }
        if (!cross.getRoadId4().equals("-1")) {
            if (cross.getRoadId4().equals(nowRoad))
                now = 3;
            if (cross.getRoadId4().equals(nextRoad))
                next = 3;
        }
        if ((((now + 2) & 3) == next)) {
            turnInfo.setTurnDirection(TurnDirection.STRAIGHT);
            return turnInfo;
        }
        List<String> roads = new ArrayList<>();
        if (((now + 1) & 3) == next) {
            turnInfo.setTurnDirection(TurnDirection.LFFT);
            roads.add(cross.getRoad(((now + 3) & 3)));//直行冲突
            turnInfo.setRoads(roads);
            return turnInfo;
        }
        if (((now + 3) & 3) == next) {
            turnInfo.setTurnDirection(TurnDirection.RIGHT);
            roads.add(cross.getRoad(((now + 1) & 3)));//直行冲突
            roads.add(cross.getRoad(((now + 2) & 3)));
            turnInfo.setRoads(roads);
            return turnInfo;
        }
        return null;
    }


    /**
     * 获取某道路某方向的第一优先级车辆
     *
     * @param channels
     * @return
     */
    private CarOnRoad getFirstPriorityCar(List<Channel> channels) {
        List<CarOnRoad> carOnRoads;
        CarOnRoad carOnRoad = null;
        boolean flag = false, flag2 = false;
        for (Channel channel : channels) {
            carOnRoads = channel.getCars();
            if (carOnRoads != null) {
                for (CarOnRoad item : carOnRoads) {
                    if (!CarStatus.WAIT.equals(item.getCarStatus())) {
                        break;
                    } else {
                        carOnRoad = item;
                        carOnRoad.setChannel(channel);
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        for (Channel channel : channels) {
            carOnRoads = channel.getCars();
            if (carOnRoads != null) {
                for (CarOnRoad item : carOnRoads) {
                    if (!CarStatus.WAIT.equals(item.getCarStatus())) {
                        break;
                    } else if (carOnRoad.getPosition() < item.getPosition()) {
                        carOnRoad = item;
                        carOnRoad.setChannel(channel);
                        flag2 = true;
                        break;
                    } else {
                        break;
                    }
                }
                if (flag2) {
                    break;
                }
            }
        }
        return carOnRoad;
    }

    /**
     * 判断某条路上某个方向是否还有wait状态的车辆
     *
     * @param roadCondition
     * @return
     */
    private boolean hasWaitingCarOnRoad(RoadCondition roadCondition, String crossId) {
        List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition, crossId);
        List<CarOnRoad> carOnRoads;
        if (channels != null) {
            for (Channel channel : channels) {
                carOnRoads = channel.getCars();
                if (carOnRoads != null && carOnRoads.size() != 0) {
                    for (CarOnRoad carOnRoad : carOnRoads) {
                        if (CarStatus.WAIT.equals(carOnRoad.getCarStatus())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 在初始化RoadSim时，需要按照编号升序加入channel
     *
     * @param roadCondition
     * @param crossId
     * @return
     */
    private List<Channel> getChannelsFromRoadOnOneDirection(RoadCondition roadCondition, String crossId) {
        List<Channel> res = new ArrayList<>();
        for (Channel channel : roadCondition.getChannels()) {
            if (channel.getToId().equals(crossId)) {
                res.add(channel);
            }
        }
        return res;
    }

    /**
     * 通过目的道路id以及路口id得到目的道路车道
     *
     * @param roadId
     * @param crossId
     * @return
     */
    private List<Channel> getChannelsFromRoadOnOneDirection(String roadId, String crossId) {
        RoadCondition roadCondition = ROAD_CONDITION_MAP.get(roadId);
        List<Channel> res = new ArrayList<>();
        for (Channel channel : roadCondition.getChannels()) {
            if (channel.getFromId().equals(crossId)) {
                res.add(channel);
            }
        }
        return res;
    }


    /**
     * 标记车辆
     *
     * @param roadConditions
     */
    /*public void markCars(List<RoadCondition> roadConditions) {
        for (RoadCondition roadCondition : roadConditions) {
            driveAllCarJustOnRoadToEndState(roadCondition.getChannels());
        }
    }*/

    /**
     * 标记车辆
     *
     * @param roadCondition
     */
    public void markCars(RoadCondition roadCondition) {
        driveAllCarJustOnRoadToEndState(roadCondition.getChannels());
    }

    /**
     * 标记某个道路的所有车道
     *
     * @param channels
     */
    private void driveAllCarJustOnRoadToEndState(List<Channel> channels) {
        // 遍历该道路的每一个车道
        for (Channel channel : channels) {
            driveAllCarJustOnRoadToEndStateOnSingleChannel(channel);
        }
    }

    /**
     * 标记单个车道的车辆
     *
     * @param channel
     */
    private void driveAllCarJustOnRoadToEndStateOnSingleChannel(Channel channel) {
        List<CarOnRoad> carOnRoads = channel.getCars();
        if (carOnRoads != null) {
            int size = carOnRoads.size();
            CarOnRoad currentCarOnRoad;
            for (int i = 0; i < size; i++) {
                currentCarOnRoad = carOnRoads.get(i);
                // 每个车道的第一辆车
                if (i == 0) {
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    if (speed > (channel.getRoad().getLength() - currentCarOnRoad.getPosition())) {
                        currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                    } else {
                        change_waiting_car++;
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                        //车没走？？
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        lock++;
                    }
                } else {
                    // 每个车道的其他车辆
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    CarOnRoad previousCarOnRoad = carOnRoads.get(i - 1);
                    int distance = previousCarOnRoad.getPosition() - currentCarOnRoad.getPosition();
                    boolean isBeyond = speed >= distance;
                    // 按照速度能够超越前车
                    if (isBeyond) {
                        if (CarStatus.WAIT.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                            continue;
                        } else if (CarStatus.STOP.equals(previousCarOnRoad.getCarStatus())) {
                            int prePosition = currentCarOnRoad.getPosition();
                            currentCarOnRoad.setCarStatus(CarStatus.STOP);
                            change_waiting_car++;
                            currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + distance - 1);
                            if (currentCarOnRoad.getPosition() != prePosition) {
                                lock++;
                            }
                        }
                    } else {
                        // 前进到能够到达的最大位置，并置为终止状态
                        int prePosition = currentCarOnRoad.getPosition();
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                        change_waiting_car++;
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        if (currentCarOnRoad.getPosition() != prePosition) {
                            lock++;
                        }
                    }
                }
            }
        }
    }

    private void driveRestAllCarJustOnRoadToEndStateOnSingleChannel(Channel channel) {
        List<CarOnRoad> carOnRoads = channel.getCars();
        if (carOnRoads != null) {
            int size = carOnRoads.size();
            CarOnRoad currentCarOnRoad;
            for (int i = 0; i < size; i++) {
                currentCarOnRoad = carOnRoads.get(i);
                // 每个车道的第一辆车
                if (i == 0) {
                    if (currentCarOnRoad.getCarStatus().equals(CarStatus.STOP)) {
                        break;
                    }
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    if (speed > (channel.getRoad().getLength() - currentCarOnRoad.getPosition())) {
                        currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                    } else {
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                        change_waiting_car++;
                        int prePosition = currentCarOnRoad.getPosition();
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        if (prePosition != currentCarOnRoad.getPosition()) {
                            lock++;
                        }
                    }
                } else {
                    // 每个车道的其他车辆
                    if (currentCarOnRoad.getCarStatus().equals(CarStatus.STOP)) {
                        break;
                    }
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    CarOnRoad previousCarOnRoad = carOnRoads.get(i - 1);
                    int distance = previousCarOnRoad.getPosition() - currentCarOnRoad.getPosition();
                    boolean isBeyond = speed >= distance;
                    // 按照速度能够超越前车
                    if (isBeyond) {
                        if (CarStatus.WAIT.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                        } else if (CarStatus.STOP.equals(previousCarOnRoad.getCarStatus())) {
                            int prePosition = currentCarOnRoad.getPosition();
                            currentCarOnRoad.setCarStatus(CarStatus.STOP);
                            change_waiting_car++;
                            currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + distance - 1);
                            if (prePosition != currentCarOnRoad.getPosition()) {
                                lock++;
                            }
                        }
                    } else {
                        // 前进到能够到达的最大位置，并置为终止状态
                        int prePosition = currentCarOnRoad.getPosition();
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                        change_waiting_car++;
                        if (prePosition != currentCarOnRoad.getPosition()) {
                            lock++;
                        }
                    }
                }
            }
        }
    }

    //标记剩下的车辆
    private void driveRestCarJustOnRoadToEndStateOnSingleChannel(Channel channel) {
        List<CarOnRoad> carOnRoads = channel.getCars();
        if (carOnRoads != null) {
            int size = carOnRoads.size();
            CarOnRoad currentCarOnRoad;
            for (int i = 0; i < size; i++) {
                currentCarOnRoad = carOnRoads.get(i);
                // 每个车道的第一辆车
                if (i == 0) {
                    continue;
                } else {
                    // 每个车道的其他车辆
                    if (currentCarOnRoad.getCarStatus().equals(CarStatus.STOP)) {
                        break;
                    }
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    CarOnRoad previousCarOnRoad = carOnRoads.get(i - 1);
                    int distance = previousCarOnRoad.getPosition() - currentCarOnRoad.getPosition();
                    boolean isBeyond = speed >= distance;
                    // 按照速度能够超越前车
                    if (isBeyond) {
                        if (CarStatus.WAIT.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                        } else if (CarStatus.STOP.equals(previousCarOnRoad.getCarStatus())) {
                            change_waiting_car++;
                            currentCarOnRoad.setCarStatus(CarStatus.STOP);
                            int prePosition = currentCarOnRoad.getPosition();
                            currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + distance - 1);
                            if (prePosition != currentCarOnRoad.getPosition()) {
                                lock++;
                            }
                        }
                    } else {
                        // 前进到能够到达的最大位置，并置为终止状态
                        int prePosition = currentCarOnRoad.getPosition();
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                        change_waiting_car++;
                        if (prePosition != currentCarOnRoad.getPosition()) {
                            lock++;
                        }
                    }
                }
            }
        }
    }
}
