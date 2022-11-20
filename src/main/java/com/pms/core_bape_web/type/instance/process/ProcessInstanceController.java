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
    public Integer validateTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(required = true) String taskName,
            @RequestParam(required = true) String actorName) {
        return processInstanceService.validateTask(processInstanceId, taskName, actorName);
    }
}
