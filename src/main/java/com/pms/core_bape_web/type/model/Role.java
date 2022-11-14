package com.pms.core_bape_web.type.model;

import org.json.simple.JSONObject;

public class Role {
    private String name;
    private boolean projectManager = false;

    public Role() {
    }

    public Role(String name, boolean projectManager) {
        this.name = name;
        this.projectManager = projectManager;
    }

    public Role(JSONObject jsonObject) {
        this.name = jsonObject.get("name").toString();
        this.projectManager = Boolean.parseBoolean(jsonObject.get("projectManager").toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public void setProjectManager(boolean projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Role))
            return false;
        return this.name.equals(((Role) other).getName());
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"projectManager\": \"" + projectManager + "\"\n" +
                "}";
    }
}
