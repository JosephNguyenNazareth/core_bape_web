package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.type.instance.Actor;
import com.pms.core_bape_web.type.instance.ArtifactInstance;
import com.pms.core_bape_web.type.instance.TaskInstance;
import com.pms.core_bape_web.type.model.Artifact;
import com.pms.core_bape_web.type.model.Task;
import com.pms.core_bape_web.type.model.Process;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "process_instance")
public class ProcessInstance {
    @Id
    private String id;
    private String createdTime;
    private boolean processClosed = false;
    private Map<String, String> updateDetail;
    @Transient
    private Actor creator;
    @Transient
    private Process processModel;
    @Transient
    private List<TaskInstance> taskInstanceList;
    @Transient
    private List<Actor> actorList;
    @Transient
    private List<ArtifactInstance> artifactInstanceList;

    public ProcessInstance() {
        this.updateDetail = new HashMap<>();
        this.updateDetail.put("created", LocalDateTime.now().toString());
    }

    public ProcessInstance(String id, String createdTime, boolean processClosed, Actor creator, Process processModel, List<TaskInstance> taskInstanceList, List<Actor> actorList, List<ArtifactInstance> artifactInstanceList) {
        this.id = id;
        this.createdTime = createdTime;
        this.processClosed = processClosed;
        this.creator = creator;
        this.processModel = processModel;
        this.taskInstanceList = taskInstanceList;
        this.actorList = actorList;
        this.artifactInstanceList = artifactInstanceList;
        this.updateDetail = new HashMap<>();
        this.updateDetail.put("created", LocalDateTime.now().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Actor getCreator() {
        return creator;
    }

    public void setCreator(Actor creator) {
        this.creator = creator;
    }

    public Process getProcessModel() {
        return processModel;
    }

    public void setProcessModel(Process processModel) {
        this.processModel = processModel;
    }

    public List<TaskInstance> getTaskInstanceList() {
        return taskInstanceList;
    }

    public void setTaskInstanceList(List<TaskInstance> taskInstanceList) {
        this.taskInstanceList = taskInstanceList;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public void setActorList(List<Actor> actorList) {
        this.actorList = actorList;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<ArtifactInstance> getArtifactInstanceList() {
        return artifactInstanceList;
    }

    public void setArtifactInstanceList(List<ArtifactInstance> artifactInstanceList) {
        this.artifactInstanceList = artifactInstanceList;
    }

    public boolean isProcessClosed() {
        return processClosed;
    }

    public void setProcessClosed(boolean processClosed) {
        this.processClosed = processClosed;
    }

    public ProcessInstance(Process processModel, List<Actor> actorList) {
        this.id = UUID.randomUUID().toString();
        this.processModel = processModel;
        this.actorList = actorList;
        this.setCreator();
        this.taskInstanceList = new ArrayList<>();
        this.artifactInstanceList = new ArrayList<>();
        this.initTaskInstance(processModel.getTaskList());
        this.updateDetail = new HashMap<>();
        this.updateDetail.put("created", LocalDateTime.now().toString());
    }

    public ProcessInstance(JSONObject processInfoLoaded) {
        this.id = processInfoLoaded.get("id").toString();
        this.creator = new Actor((JSONObject) processInfoLoaded.get("creator"));
        this.createdTime = processInfoLoaded.get("createdTime").toString();
        this.actorList = new ArrayList<>();
        this.taskInstanceList = new ArrayList<>();
        this.artifactInstanceList = new ArrayList<>();

        checkPointActorList((JSONArray) processInfoLoaded.get("actorList"));
        checkPointTaskList((JSONArray) processInfoLoaded.get("taskList"));
        checkPointArtifactList((JSONArray) processInfoLoaded.get("availableArtifactList"));
        this.processClosed = Boolean.parseBoolean(processInfoLoaded.get("processClosed").toString());
    }

    private void setCreator() {
        for (Actor actor : actorList) {
            if (actor.getRole().isProjectManager()) {
                this.creator = actor;
                this.createdTime = LocalDateTime.now().toString();
            }
            break;
        }
        if (this.creator == null)
            System.out.println("Please specify project manager who permits starting a process instance.");
    }

    private void initTaskInstance(List<Task> taskListModel) {
        for (Task task : taskListModel) {
            for (Actor actor : this.actorList) {
                if (actor.getRole().equals(task.getRole())) {
                    TaskInstance taskInstance = new TaskInstance(task, actor);
                    this.taskInstanceList.add(taskInstance);
                    break;
                }
            }
        }
    }

    private void checkPointActorList(JSONArray jsonArray) {
        for (Object o : jsonArray) {
            JSONObject jObject = (JSONObject) o;
            Actor actor = new Actor(jObject);
            this.actorList.add(actor);
        }
    }

    private void checkPointTaskList(JSONArray jsonArray) {
        for (Object o : jsonArray) {
            JSONObject jObject = (JSONObject) o;
            TaskInstance taskInstance = new TaskInstance(jObject);
            this.taskInstanceList.add(taskInstance);
        }
    }

    private void checkPointArtifactList(JSONArray jsonArray) {
        for (Object o : jsonArray) {
            JSONObject jObject = (JSONObject) o;
            ArtifactInstance artifactInstance = new ArtifactInstance(jObject);
            this.artifactInstanceList.add(artifactInstance);
        }
    }

    public void saveProcessInstanceInfo() {
        String instanceInfoPath =  "./src/main/resources/static/" + this.processModel.getName() + "/processInfo/";
        File processInfoFile = new File(instanceInfoPath);
        if (!processInfoFile.isDirectory()) {
            processInfoFile.mkdir();
        }

        try {
            FileWriter fileWriter = new FileWriter(instanceInfoPath + "/" + this.id + ".json");
            fileWriter.write(this.toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TaskInstance getTaskByName(String name, String actorName) {
        if (this.processClosed || this.creator == null)
            return null;
        for (TaskInstance taskInstance : this.taskInstanceList) {
            if (taskInstance.getTaskModel().getName().equals(name) && taskInstance.getActor().getName().equals(actorName))
                return taskInstance;
        }
        return null;
    }

    public boolean checkExistenceArtifact(Artifact artifact) {
        for (ArtifactInstance artifactInstance : this.artifactInstanceList) {
            if (artifactInstance.getArtifact().getName().equals(artifact.getName()) && artifactInstance.getStateMachine().getState().equals(artifact.getState()) && !artifactInstance.isLocked())
                return true;
        }
        return false;
    }

    public TaskInstance checkExistenceTaskInstance(String taskId) {
        for (TaskInstance taskInstance : this.taskInstanceList) {
            if (taskInstance.getId().equals(taskId))
                return taskInstance;
        }
        return null;
    }

    public void addArtifactInstanceList(ArtifactInstance artifactInstance) {
        this.artifactInstanceList.add(artifactInstance);
    }

    public boolean checkMultipleTaskInstanceNotCompleted(TaskInstance taskInstance) {
        for (TaskInstance refTaskInstance : this.taskInstanceList) {
            if (refTaskInstance.getTaskModel().getName().equals(taskInstance.getTaskModel().getName()) && !refTaskInstance.getId().equals(taskInstance.getId())) {
                if (!refTaskInstance.getStateMachine().getState().equals("completed"))
                    return false;
            }
        }
        return true;
    }

    public void update(String details) {
        this.updateDetail.put(details, LocalDateTime.now().toString());
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"id\": \"" + id + "\",\n" +
                "\"name\": \"" + this.processModel.getName() + "\",\n" +
                "\"creator\": " + creator + ",\n" +
                "\"createdTime\": \" " + createdTime + "\",\n" +
                "\"updateDetail\": " + updateDetail + ",\n" +
                "\"taskList\": " + taskInstanceList + ",\n" +
                "\"actorList\": " + actorList + ",\n" +
                "\"availableArtifactList\": " + artifactInstanceList + ",\n" +
                "\"processClosed\": \"" + processClosed + "\"\n" +
                "}";
    }
}
