package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.type.instance.Actor;
import com.pms.core_bape_web.type.instance.PreDefinedArtifactInstance;
import com.pms.core_bape_web.type.instance.TaskInstance;
import com.pms.core_bape_web.type.model.Artifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/process-instance")
public class ProcessInstanceController {
    private final ProcessInstanceService processInstanceService;

    @Autowired
    public ProcessInstanceController(ProcessInstanceService processInstanceService) {
        this.processInstanceService = processInstanceService;
    }

    @GetMapping
    public List<ProcessInstance> getProcessInstances() {
        return processInstanceService.getProcessInstances();
    }

    @GetMapping(path = "name/{processInstanceName}")
    public List<ProcessInstance> getProcessInstancesByName() {
        return processInstanceService.getProcessInstances();
    }

    @GetMapping(path = "{processInstanceId}")
    public ProcessInstance getProcessInstance(@PathVariable String processInstanceId) {
        return processInstanceService.getProcessInstance(processInstanceId);
    }

    @PostMapping
    public String addNewProcessInstance(
            @RequestParam(required = true) String processName,
            @RequestParam(required = true) String creatorName) {
        return processInstanceService.addNewProcessInstance(processName, creatorName);
    }

    @PutMapping(path = "{processInstanceId}/change-state")
    public void changeStateProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) Boolean processInstanceState) {
        processInstanceService.changeState(processInstanceId, processInstanceState);
    }

    @GetMapping(path = "{processInstanceId}/get-task")
    public List<String> getTask(
            @PathVariable("processInstanceId") String processInstanceId) {
        return processInstanceService.getTask(processInstanceId);
    }

    @PutMapping(path = "{processInstanceId}/start-task")
    public String startTask(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskName,
            @RequestParam(required = true) String actorName) {
        return processInstanceService.startTask(processInstanceId, taskName, actorName);
    }

    @PutMapping(path = "{processInstanceId}/end-task")
    public void endTask(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskId,
            @RequestBody(required = true) List<PreDefinedArtifactInstance> preDefinedArtifactInstanceList) {
        processInstanceService.endTask(processInstanceId, taskId, preDefinedArtifactInstanceList);
    }

    @GetMapping(path = "{processInstanceId}/validate-task")
    public TaskInstance validateTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskName,
            @RequestParam(required = true) String actorName) {
        return processInstanceService.validateTask(processInstanceId, taskName, actorName);
    }

    @GetMapping(path = "{processInstanceId}/data-object")
    public List<Artifact> getArtifact(@PathVariable("processInstanceId") String processInstanceId) {
        return processInstanceService.getArtifact(processInstanceId);
    }

    @GetMapping(path = "{processInstanceId}/users")
    public List<Actor> getUsers(@PathVariable("processInstanceId") String processInstanceId) {
        return processInstanceService.getActors(processInstanceId);
    }

    @GetMapping(path = "log")
    public String getLog() {
        return processInstanceService.getLog();
    }
}
