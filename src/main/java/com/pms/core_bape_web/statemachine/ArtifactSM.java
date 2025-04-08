package com.pms.core_bape_web.statemachine;

import com.pms.core_bape_web.type.instance.ArtifactInstance;
import org.json.simple.JSONObject;

public class ArtifactSM {
    private String currentArtifactId;
    private String state;

    public String getCurrentArtifact() {
        return currentArtifactId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state.equals("init") || state.equals("defined") || state.equals("modified"))
            this.state = state;
    }

    public ArtifactSM() {
    }

    public ArtifactSM(ArtifactInstance currentArtifact) {
        this.currentArtifactId = currentArtifact.getId();
        this.state = "init";
    }

    public ArtifactSM(JSONObject jsonObject, ArtifactInstance artifactInstance) {
        this.currentArtifactId = artifactInstance.getId();
        this.state = jsonObject.get("state").toString();
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"state\": \"" + state + "\"\n" +
                "}";
    }
}
