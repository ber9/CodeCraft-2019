package com.huawei.entity;

import java.util.List;

/**
 * @author 郭世明
 * @date 2019/3/17 20:28
 */
public class Channel {

    private String id;
    private Road road;
    private String fromId;
    private String toId;
    private List<CarOnRoad> cars;

    public Channel(String id, Road road, String fromId, String toId, List<CarOnRoad> cars) {
        this.id = id;
        this.road = road;
        this.fromId = fromId;
        this.toId = toId;
        this.cars = cars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public List<CarOnRoad> getCars() {
        return cars;
    }

    public void setCars(List<CarOnRoad> cars) {
        this.cars = cars;
    }
}
