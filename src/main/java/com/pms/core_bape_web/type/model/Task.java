package com.pms.core_bape_web.type.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;
    private List<Artifact> input;
    private List<Artifact> output;
    private Role role;

    public Task() {
    }

    public Task(String name, Role role) {
        this.name = name;
        this.role = role;
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artifact> getInput() {
        return input;
    }

    public void setInput(List<Artifact> input) {
        this.input = input;
    }

    public void addInput(Artifact input) {
        this.input.add(input);
    }

    public List<Artifact> getOutput() {
        return output;
    }

    public void setOutput(List<Artifact> output) {
        this.output = output;
    }

    public void addOutput(Artifact output) {
        this.output.add(output);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", input=" + input +
                ", output=" + output +
                ", role=" + role +
                '}';
    }
}
