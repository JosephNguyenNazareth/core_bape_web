package com.pms.core_bape_web.type.instance;

import com.pms.core_bape_web.statemachine.TaskSM;
import com.pms.core_bape_web.type.model.Artifact;
import com.pms.core_bape_web.type.model.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskInstance {
    private Task taskModel;
    private String id;
    private List<ArtifactInstance> inputInstanceList;
    private List<ArtifactInstance> outputInstanceList;
    private TaskSM stateMachine;
    private Actor actor;

    public TaskInstance() {
    }

    public String getId() {
        return id;
    }

    public List<ArtifactInstance> getInputInstanceList() {
        return inputInstanceList;
    }

    public List<ArtifactInstance> getOutputInstanceList() {
        return outputInstanceList;
    }

    public TaskSM getStateMachine() {
        return stateMachine;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Task getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(Task taskModel) {
        this.taskModel = taskModel;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInputInstanceList(List<ArtifactInstance> inputInstanceList) {
        this.inputInstanceList = inputInstanceList;
    }

    public void setOutputInstanceList(List<ArtifactInstance> outputInstanceList) {
        this.outputInstanceList = outputInstanceList;
    }

    public void setStateMachine(TaskSM stateMachine) {
        this.stateMachine = stateMachine;
    }

    public TaskInstance(Task taskModel, Actor actor) {
        this.taskModel = taskModel;
        this.id = UUID.randomUUID().toString();
        this.stateMachine = new TaskSM(this);
        this.actor = actor;

        this.inputInstanceList = new ArrayList<>();
        this.outputInstanceList = new ArrayList<>();
    }

    public TaskInstance(TaskInstance anotherTaskInstance, Actor actor) {
        this.id = UUID.randomUUID().toString();
        this.stateMachine = new TaskSM(this);
        this.actor = actor;

        this.inputInstanceList = new ArrayList<>();
        this.outputInstanceList = new ArrayList<>();
    }

    public TaskInstance(JSONObject jsonObject) {
        this.id = jsonObject.get("id").toString();
        this.actor = new Actor((JSONObject) jsonObject.get("actor"));
        this.inputInstanceList = new ArrayList<>();
        this.outputInstanceList = new ArrayList<>();

        checkpointArtifactInstanceList(this.inputInstanceList, (JSONArray) jsonObject.get("inputInstanceList"));
        checkpointArtifactInstanceList(this.outputInstanceList, (JSONArray) jsonObject.get("outputInstanceList"));
        checkpointArtifactList(this.taskModel.getInput(), (JSONArray) jsonObject.get("inputList"));
        checkpointArtifactList(this.taskModel.getOutput(), (JSONArray) jsonObject.get("outputList"));
    }

    private void checkpointArtifactList(List<Artifact> artifactList, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jObject = (JSONObject) jsonArray.get(i);
            Artifact taskInstance = new Artifact(jObject);
            artifactList.add(taskInstance);
        }
    }

    private void checkpointArtifactInstanceList(List<ArtifactInstance> artifactInstanceList, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jObject = (JSONObject) jsonArray.get(i);
            ArtifactInstance taskInstance = new ArtifactInstance(jObject);
            artifactInstanceList.add(taskInstance);
        }
    }

    public boolean verifyArtifactState(String name, String updateState) {
        for (Artifact outputArtifact : this.taskModel.getOutput()) {
            if (name.equals(outputArtifact.getName()) && updateState.equals(outputArtifact.getState()))
                return true;
        }
        return false;
    }

    public void updateArtifactSharedStatus(String artifactName, Integer isShared) {
        for (ArtifactInstance outputArtifactInstance : this.outputInstanceList) {
            if (artifactName.equals(outputArtifactInstance.getArtifact().getName())) {
                outputArtifactInstance.setShared(isShared);
                outputArtifactInstance.setLocked(true);
            }
        }
    }

    public void loadInputArtifact(List<ArtifactInstance> globalArtifactInstanceList) {
        for (Artifact input : this.taskModel.getInput()) {
            boolean inGlobal = false;
            for (ArtifactInstance globalArtifactInstance : globalArtifactInstanceList) {
                if (input.getName().equals(globalArtifactInstance.getArtifact().getName())) {
                    this.inputInstanceList.add(globalArtifactInstance);
                    inGlobal = true;
                }
            }
            if (!inGlobal) {
                ArtifactInstance inputInstance = new ArtifactInstance(input);
                this.inputInstanceList.add(inputInstance);
            }
        }
    }

    public ArtifactInstance exportOutputArtifact(String artifactName, String state, Integer sharedArtifact) {
        for (Artifact output : this.taskModel.getOutput()) {
            if (output.getName().equals(artifactName)) {
                ArtifactInstance outputInstance = new ArtifactInstance(output);
                outputInstance.setShared(sharedArtifact);
                outputInstance.getStateMachine().setState(state);
                this.outputInstanceList.add(outputInstance);
                return outputInstance;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"id\": \"" + id + "\",\n" +
                "\"name\": \"" + this.taskModel.getName() + "\",\n" +
                "\"inputInstanceList\": " + inputInstanceList + ",\n" +
                "\"outputInstanceList\": " + outputInstanceList + ",\n" +
                "\"actor\": " + actor + ",\n" +
                "\"stateMachine\": " + stateMachine + "\n" +
                "}";
    }
}
