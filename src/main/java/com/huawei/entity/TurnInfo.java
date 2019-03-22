package com.huawei.entity;

import java.util.List;

public class TurnInfo {

    private TurnDirection turnDirection;
    private List<String> roads;

    public TurnInfo(TurnDirection turnDirection, List<String> roads) {
        this.turnDirection = turnDirection;
        this.roads = roads;
    }

    public TurnInfo(TurnDirection turnDirection) {
        this.turnDirection = turnDirection;
    }

    public TurnDirection getTurnDirection() {
        return turnDirection;
    }

    public void setTurnDirection(TurnDirection turnDirection) {
        this.turnDirection = turnDirection;
    }

    public List<String> getRoads() {
        return roads;
    }

    public void setRoads(List<String> roads) {
        this.roads = roads;
    }

    public TurnInfo() {
    }
}
