package com.huawei.util;

import com.huawei.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 郭世明
 * @date 2019/3/17 14:56
 */
public class ScheduleSim {

    private static final int IS_DUPLEX = 1;

    private static final int MAX_TIMES = 5000;

    private static final Map<String, Road> ROAD_MAP = new HashMap<>(100);

    private static final Map<String, Road> CAR_MAP = new HashMap<>(10000);

    private static final Map<String, Cross> CROSS_MAP = new HashMap<>(60);


    /**
     * 调度某一道路车辆
     *
     * @param roadCondition
     * @param crossId
     */
    public void scheduleOneRoadCars(RoadCondition roadCondition, String crossId) {
        List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition, crossId);

        while (hasWaitingCarOnRoad(roadCondition, crossId)) {

        }
        for (Channel channel : channels) {
            // 某个道路的第n个车道的第一辆车
            CarOnRoad carOnRoad = channel.getCars().get(0);
            // 不是等待状态
            if (!CarStatus.WAIT.equals(carOnRoad.getCarStatus())) {
                continue;
            }
            // 判断是否冲突

            // 不冲突就移动
            // 到达终点判断，queue size

            // 重新标记该车道上的状态

        }
    }

    private boolean isConflict(CarOnRoad car, String crossId) {
        if(car.getPath().size()==0)//到终点
            return false;
        Cross cross = CROSS_MAP.get(crossId);
        TurnInfo turnInfo = turnDirection(cross, car);//转向冲突车道信息
        return checkConflict(turnInfo,cross);
    }

    private boolean checkConflict(TurnInfo turnInfo, Cross cross){
        if(turnInfo.getTurnDirection().equals(TurnDirection.STRAIGHT))
            return false;
        if(turnInfo.getTurnDirection().equals(TurnDirection.LFFT)){
            String road = turnInfo.getRoads().get(0);
            RoadCondition roadCondition = ;
            List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition,cross.getId());
            CarOnRoad conflictCar = getFirstPriorityCar(channels);
            if (turnDirection(cross,conflictCar).getTurnDirection().equals(TurnDirection.STRAIGHT))
                return true;
            else
                return false;
        }
        if(turnInfo.getTurnDirection().equals(TurnDirection.RIGHT)){
            String sRoad = turnInfo.getRoads().get(0);
            RoadCondition roadCondition = ;
            List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition,cross.getId());
            CarOnRoad sConflictCar = getFirstPriorityCar(channels);
            String lRoad = turnInfo.getRoads().get(1);
            roadCondition = ;
            channels = getChannelsFromRoadOnOneDirection(roadCondition,cross.getId());
            CarOnRoad lConflictCar = getFirstPriorityCar(channels);
            if (turnDirection(cross,sConflictCar).getTurnDirection().equals(TurnDirection.STRAIGHT)
                    ||turnDirection(cross,lConflictCar).getTurnDirection().equals(TurnDirection.LFFT))
                return true;
            else
                return false;
        }
    }

    /**
     * 转向信息
     * @param cross
     * @return 方向以及冲突车道
     */
    private TurnInfo turnDirection(Cross cross, CarOnRoad car) {
        String nowRoad = car.getRoadId();
        if(car.getPath().size()==0)//到达终点直接设置右转，不耽误其他车辆判断
            return new TurnInfo(TurnDirection.RIGHT);
        String nextRoad = car.getPath().peek();
        TurnInfo turnInfo = new TurnInfo();
        int now = 0, next = 0;
        if (cross.getRoadId1() != null) {
            if (cross.getRoadId1().equals(nowRoad))
                now = 0;
            else if (cross.getRoadId1().equals(nextRoad))
                next = 0;
        }
        if (cross.getRoadId2() != null) {
            if (cross.getRoadId2().equals(nowRoad))
                now = 1;
            if (cross.getRoadId2().equals(nextRoad))
                next = 1;
        }
        if (cross.getRoadId3() != null) {
            if (cross.getRoadId3().equals(nowRoad))
                now = 2;
            if (cross.getRoadId3().equals(nextRoad))
                next = 2;
        }
        if (cross.getRoadId4() != null) {
            if (cross.getRoadId4().equals(nowRoad))
                now = 3;
            if (cross.getRoadId4().equals(nextRoad))
                next = 3;
        }
        if ((now + 2) % 4 == next) {
            turnInfo.setTurnDirection(TurnDirection.STRAIGHT);
            return turnInfo;
        }
        List<String> roads = new ArrayList<>();
        if ((now + 1) % 4 == next) {
            turnInfo.setTurnDirection(TurnDirection.LFFT);
            roads.add(cross.getRoad((now + 3) % 4));//直行冲突
            turnInfo.setRoads(roads);
            return turnInfo;
        }
        if ((now + 3) % 4 == next) {
            turnInfo.setTurnDirection(TurnDirection.RIGHT);
            roads.add(cross.getRoad((now + 1) % 4));//直行冲突
            roads.add(cross.getRoad((now + 2) % 4));
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
        for (Channel channel : channels) {
            carOnRoads = channel.getCars();
            if (carOnRoads != null) {
                for (CarOnRoad item : carOnRoads) {
                    if (!CarStatus.WAIT.equals(item.getCarStatus())) {
                        break;
                    } else {
                        carOnRoad = item;
                    }
                }
            }
        }
        for (Channel channel : channels) {
            carOnRoads = channel.getCars();
            if (carOnRoads != null) {
                for (CarOnRoad item : carOnRoads) {
                    if (CarStatus.WAIT.equals(item.getCarStatus()) && carOnRoad.getPosition() < item.getPosition()) {
                        carOnRoad = item;
                    }
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
                if (carOnRoads != null) {
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
     * 标记车辆
     *
     * @param roadConditions
     */
    public void markCars(List<RoadCondition> roadConditions) {
        for (RoadCondition roadCondition : roadConditions) {
            driveAllCarJustOnRoadToEndState(roadCondition.getChannels());
        }
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
                    currentCarOnRoad.setCarStatus(speed > (channel.getRoad().getLength() - currentCarOnRoad.getPosition()) ? CarStatus.WAIT : CarStatus.STOP);
                } else {
                    // 每个车道的其他车辆
                    int speed = Math.min(channel.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    CarOnRoad previousCarOnRoad = carOnRoads.get(i - 1);
                    int distance = previousCarOnRoad.getPosition() - currentCarOnRoad.getPosition();
                    boolean isBeyond = speed > distance;
                    // 按照速度能够超越前车
                    if (isBeyond) {
                        if (CarStatus.WAIT.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.WAIT);
                        } else if (CarStatus.STOP.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.STOP);
                            currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + distance - 1);
                        }
                    } else {
                        // 前进到能够到达的最大位置，并置为终止状态
                        currentCarOnRoad.setPosition(currentCarOnRoad.getPosition() + speed);
                        currentCarOnRoad.setCarStatus(CarStatus.STOP);
                    }
                }
            }
        }
    }
}
