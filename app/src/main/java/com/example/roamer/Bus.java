package com.example.roamer;

public class Bus {

    private int busId;
    private String busName;
    private int roadId;

    public Bus(int busId, String busName, int roadId) {
        this.busId = busId;
        this.busName = busName;
        this.roadId = roadId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }
}
