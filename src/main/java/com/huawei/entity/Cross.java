package com.huawei.entity;

/**
 * @author 郭世明
 * @date 2019/3/16 18:16
 */
public class Cross {

    private String id;
    private String roadId1;
    private String roadId2;
    private String roadId3;
    private String roadId4;

    public Cross(String id, String roadId1, String roadId2, String roadId3, String roadId4) {
        this.id = id;
        this.roadId1 = roadId1;
        this.roadId2 = roadId2;
        this.roadId3 = roadId3;
        this.roadId4 = roadId4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoadId1() {
        return roadId1;
    }

    public void setRoadId1(String roadId1) {
        this.roadId1 = roadId1;
    }

    public String getRoadId2() {
        return roadId2;
    }

    public void setRoadId2(String roadId2) {
        this.roadId2 = roadId2;
    }

    public String getRoadId3() {
        return roadId3;
    }

    public void setRoadId3(String roadId3) {
        this.roadId3 = roadId3;
    }

    public String getRoadId4() {
        return roadId4;
    }

    public void setRoadId4(String roadId4) {
        this.roadId4 = roadId4;
    }
}
