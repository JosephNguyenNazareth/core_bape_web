package com.pms.core_bape_web.type.model;

import org.json.simple.JSONObject;

public class Artifact {
    private String name;
    private String type; // input or output
    private String state; // init, modified or defined

    public Artifact() {
    }

    public Artifact(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Artifact(String name, String type, String state) {
        this.name = name;
        this.type = type;
        this.state = state;
    }

    public Artifact(JSONObject jsonObject) {
        this.name = jsonObject.get("name").toString();
        this.type = jsonObject.get("type").toString();
        this.state = jsonObject.get("state").toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"type\": \"" + type + "\",\n" +
                "\"state\": \"" + state + "\"\n" +
                "}";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Artifact))
            return false;
        Artifact otherArtifact = (Artifact) other;
        return this.name.equals(otherArtifact.getName()) && this.type.equals(otherArtifact.getType()) && this.state.equals(otherArtifact.getState());
    }
}
