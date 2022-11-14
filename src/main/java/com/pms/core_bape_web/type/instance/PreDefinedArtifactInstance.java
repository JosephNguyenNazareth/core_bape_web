package com.pms.core_bape_web.type.instance;

public class PreDefinedArtifactInstance {
    private String name;
    private String state;
    private Integer shared;

    public PreDefinedArtifactInstance(String name, String state, Integer shared) {
        this.name = name;
        this.state = state;
        this.shared = shared;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getShared() {
        return shared;
    }

    public void setShared(Integer shared) {
        this.shared = shared;
    }
}
