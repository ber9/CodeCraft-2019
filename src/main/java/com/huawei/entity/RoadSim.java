package main.java.com.huawei.entity;

import java.util.List;

/**
 * @author 郭世明
 * @date 2019/3/16 18:33
 */
public class RoadSim {

    private List<List<CarOnRoad>> cars;
    private Road road;

    public RoadSim(List<List<CarOnRoad>> cars, Road road) {
        this.cars = cars;
        this.road = road;
    }

    public List<List<CarOnRoad>> getCars() {
        return cars;
    }

    public void setCars(List<List<CarOnRoad>> cars) {
        this.cars = cars;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }
}
