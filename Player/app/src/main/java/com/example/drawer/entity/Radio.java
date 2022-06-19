package com.example.drawer.entity;

import java.io.Serializable;

public class Radio implements Serializable {
    private String name;
    private String id;
    private String coverUrl;
    private String detital;
    private String palyCount;

    public String getPalyCount() {
        return palyCount;
    }

    public void setPalyCount(String palyCount) {
        this.palyCount = palyCount;
    }

    public String getDetital() {
        return detital;
    }

    public void setDetital(String detital) {
        this.detital = detital;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
