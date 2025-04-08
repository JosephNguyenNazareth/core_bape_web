package com.pms.core_bape_web.statemachine;

import com.pms.core_bape_web.type.instance.TaskInstance;

public class TaskSM {
    private String currentTaskId;
    private String state;

    public String getCurrentTask() {
        return currentTaskId;
    }

    public String getState() {
        return state;
    }

    public TaskSM() {
    }

    public TaskSM(TaskInstance currentTask) {
        this.currentTaskId = currentTask.getId();
        this.state = "init";
        System.out.println(currentTask.getTaskModel().getName() + " in state " + this.state);
    }

    public void evolve() {
        switch (state) {
            case "init":
                state = "inProgress";
                break;
            case "inProgress":
                state = "completed";
        }
        System.out.println(currentTaskId + " in state " + this.state);
    }

    public void lock() {
        switch (state) {
            case "init":
                state = "waiting";
        }
        System.out.println(currentTaskId + " in state " + this.state);
    }

    public void unlock() {
        switch (state) {
            case "waiting":
                state = "inProgress";
        }
        System.out.println(currentTaskId + " in state " + this.state);
    }

    public void reset() {
        state = "init";
        System.out.println(currentTaskId + " in state " + this.state);
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"state\": \"" + state + "\"\n" +
                "}";
    }
}
