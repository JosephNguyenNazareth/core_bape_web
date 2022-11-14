package com.pms.core_bape_web.type.instance;

import com.pms.core_bape_web.type.model.Role;
import org.json.simple.JSONObject;

public class Actor {
    private String name;
    private Role role;

    public Actor() {
    }

    public Actor(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public Actor(JSONObject jsonObject) {
        this.name = jsonObject.get("name").toString();
        this.role = new Role((JSONObject)jsonObject.get("role"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"role\": " + role + "\n" +
                "}";
    }
}
