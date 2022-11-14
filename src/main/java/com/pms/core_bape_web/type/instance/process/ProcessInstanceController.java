package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.type.instance.PreDefinedArtifactInstance;
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

    @PostMapping
    public void addNewProcessInstance(
            @RequestParam(required = true) String processName,
            @RequestParam(required = true) String creatorName) {
        processInstanceService.addNewProcessInstance(processName, creatorName);
    }

    @PutMapping(path = "{processInstanceId}")
    public void changeStateProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) Boolean processInstanceState) {
        processInstanceService.changeState(processInstanceId, processInstanceState);
    }

    @PutMapping(path = "{processInstanceId}/start-task")
    public void startTask(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskName,
            @RequestParam(required = true) String actorName) {
        processInstanceService.startTask(processInstanceId, taskName, actorName);
    }

    @PutMapping(path = "{processInstanceId}/end-task")
    public void endTask(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskId,
            @RequestBody(required = true) List<PreDefinedArtifactInstance> preDefinedArtifactInstanceList) {
        processInstanceService.endTask(processInstanceId, taskId, preDefinedArtifactInstanceList);
    }
}
