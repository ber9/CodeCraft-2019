package com.huawei.entity;

import java.util.List;

/**
 * @author 郭世明
 * @date 2019/3/16 18:33
 */
public class RoadCondition {

    private List<Channel> channels;
    private Road road;

    public RoadCondition(List<Channel> channels, Road road) {
        this.channels = channels;
        this.road = road;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }
}
