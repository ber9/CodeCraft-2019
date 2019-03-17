package com.huawei.util;

import com.huawei.entity.CarOnRoad;
import com.huawei.entity.CarStatus;
import com.huawei.entity.Road;
import com.huawei.entity.RoadSim;

import java.util.List;

/**
 * @author 郭世明
 * @date 2019/3/17 14:56
 */
public class ScheduleSim {

    private static final int IS_DUPLEX = 1;

    private static final int MAX_TIMES = 5000;

    public void markCars(List<RoadSim> roadSims) {
        for (RoadSim roadSim : roadSims) {
            driveAllCarJustOnRoadToEndState(roadSim);
        }
    }

    public void driveAllCarJustOnRoadToEndState(RoadSim roadSim) {
        Road road = roadSim.getRoad();
        boolean flag = road.isDuplex().equals(IS_DUPLEX);
        int middle = roadSim.getCars().size() - 1;
        /*// 如果为双向车道，middle之前的为from到to的车道，middle之后的为to到from的车道
        if (flag) {
            middle = middle >> 1;
        }*/
        // 遍历每一个车道
        List<CarOnRoad> carOnRoads;
        for (int i = 0; i <= middle; i++) {
            carOnRoads = roadSim.getCars().get(i);
            int size = carOnRoads.size();
            CarOnRoad currentCarOnRoad;
            for (int j = 0; j < size; j++) {
                currentCarOnRoad = carOnRoads.get(j);
                // 每个车道的第一辆车
                if (j == 0) {
                    int speed = Math.min(roadSim.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    currentCarOnRoad.setCarStatus(speed > (road.getLength() - currentCarOnRoad.getPosition()) ? CarStatus.WAITING : CarStatus.STOP);
                } else {
                    // 每个车道的其他车辆
                    int speed = Math.min(roadSim.getRoad().getSpeed(), currentCarOnRoad.getCar().getSpeed());
                    CarOnRoad previousCarOnRoad = carOnRoads.get(j - 1);
                    int distance = previousCarOnRoad.getPosition() - currentCarOnRoad.getPosition();
                    boolean isBeyond = speed > distance;
                    // 按照速度能够超越前车
                    if (isBeyond) {
                        if (CarStatus.WAITING.equals(previousCarOnRoad.getCarStatus())) {
                            currentCarOnRoad.setCarStatus(CarStatus.WAITING);
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
