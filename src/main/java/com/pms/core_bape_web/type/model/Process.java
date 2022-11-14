package com.pms.core_bape_web.type.model;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private String name;
    private List<Task> taskList;

    public Process() {
    }

    public Process(String name) {
        this.name = name;
        this.taskList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void addTask(Task task) {
        this.taskList.add(task);
    }

    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", taskList=" + taskList +
                '}';
    }
}
