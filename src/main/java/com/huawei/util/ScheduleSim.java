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

    private static final Map<String,Road> ROAD_MAP = new HashMap<>(16);



    public void scheduleOneRoadCars(RoadCondition roadCondition, String crossId){
        List<Channel> channels = getChannelsFromRoadOnOneDirection(roadCondition,crossId);
        for (Channel channel : channels){
            // 某个道路的第n个车道的第一辆车
            CarOnRoad carOnRoad = channel.getCars().get(0);
            // 不是等待状态
            if (!CarStatus.WAIT.equals(carOnRoad.getCarStatus())){
                continue;
            }
            // 判断是否冲突

            // 不冲突就移动

            // 重新标记该车道上的状态

        }
    }

    /**
     * 在初始化RoadSim时，需要按照编号升序加入channel
     * @param roadCondition
     * @param crossId
     * @return
     */
    private List<Channel> getChannelsFromRoadOnOneDirection(RoadCondition roadCondition, String crossId){
        List<Channel> res = new ArrayList<>();
        for (Channel channel : roadCondition.getChannels()){
            if (channel.getToId().equals(crossId)){
                res.add(channel);
            }
        }
        return res;
    }

    public void markCars(List<RoadCondition> roadConditions) {
        for (RoadCondition roadCondition : roadConditions) {
            driveAllCarJustOnRoadToEndState(roadCondition.getChannels());
        }
    }

    /**
     * 标记某个道路的所有车道
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
     * @param channel
     */
    private void driveAllCarJustOnRoadToEndStateOnSingleChannel(Channel channel) {
        List<CarOnRoad> carOnRoads = channel.getCars();
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
