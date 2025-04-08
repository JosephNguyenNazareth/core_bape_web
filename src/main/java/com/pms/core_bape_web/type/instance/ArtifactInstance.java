package com.pms.core_bape_web.type.instance;

import com.pms.core_bape_web.statemachine.ArtifactSM;
import com.pms.core_bape_web.type.model.Artifact;
import org.json.simple.JSONObject;

import java.util.UUID;

public class ArtifactInstance {
    private Artifact artifact;
    private String id;
    private Integer shared; // -1 if no share between different task names, 0 if no share between same task name, 1 if share between same task name
    private ArtifactSM stateMachine;
    private Boolean isLocked;



    public String getId() {
        return id;
    }

    public ArtifactSM getStateMachine() {
        return stateMachine;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public ArtifactInstance(Artifact artifact) {
        this.id = UUID.randomUUID().toString();
        this.artifact = artifact;
        this.stateMachine = new ArtifactSM(this);
        this.shared = -1;
        this.isLocked = false;
    }

    public ArtifactInstance() {
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Integer isShared() {
        return shared;
    }

    public void setShared(Integer shared) {
        this.shared = shared;
    }

    public ArtifactInstance(JSONObject jsonObject) {
        this.id = jsonObject.get("id").toString();
        stateMachine = new ArtifactSM((JSONObject) jsonObject.get("stateMachine"), this);
        this.shared = Integer.parseInt(jsonObject.get("shared").toString());
        this.isLocked = Boolean.parseBoolean(jsonObject.get("isLocked").toString());
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"id\": \"" + id + "\",\n" +
                "\"name\": \"" + this.artifact.getName() + "\",\n" +
                "\"type\": \"" + this.artifact.getType() + "\",\n" +
                "\"shared\": " + shared + ",\n" +
                "\"isLocked\": \"" + shared + "\",\n" +
                "\"expectedState\": \"" + this.artifact.getState() + "\",\n" +
                "\"stateMachine\": " + stateMachine + "\n" +
                "}";
    }
}
