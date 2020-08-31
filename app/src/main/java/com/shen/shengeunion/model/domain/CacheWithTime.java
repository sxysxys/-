package com.shen.shengeunion.model.domain;

public class CacheWithTime {
    private long duration;

    private String jsonVal;

    public CacheWithTime(long duration, String jsonVal) {
        this.duration = duration;
        this.jsonVal = jsonVal;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getJsonVal() {
        return jsonVal;
    }

    public void setJsonVal(String jsonVal) {
        this.jsonVal = jsonVal;
    }
}
