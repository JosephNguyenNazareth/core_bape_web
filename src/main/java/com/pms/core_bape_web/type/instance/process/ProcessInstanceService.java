package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.casestudy.CaseStudyBape;
import com.pms.core_bape_web.casestudy.CaseStudyBapeImpl;
import com.pms.core_bape_web.type.instance.ArtifactInstance;
import com.pms.core_bape_web.type.instance.PreDefinedArtifactInstance;
import com.pms.core_bape_web.type.instance.TaskInstance;
import com.pms.core_bape_web.type.model.Artifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProcessInstanceService {
    private final ProcessInstanceRepository processInstanceRepository;

    @Autowired
    public ProcessInstanceService(ProcessInstanceRepository processInstanceRepository) {
        this.processInstanceRepository = processInstanceRepository;
    }

    public List<ProcessInstance> getProcessInstances() {
        return processInstanceRepository.findAll();
    }

    public ProcessInstance getProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        return processInstance;
    }
    public String addNewProcessInstance(String processName, String creatorName) {
        CaseStudyBape caseStudyBape = new CaseStudyBapeImpl(processName);
        if (caseStudyBape.getName() != null)
            caseStudyBape.loadProcess(creatorName);
        processInstanceRepository.save(caseStudyBape.getProcessInstance());
        return caseStudyBape.getProcessInstance().getId();
    }


    @Transactional
    public void changeState(String processInstanceId, Boolean state) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        processInstanceFound.setProcessClosed(state);
        processInstanceRepository.save(processInstanceFound);
    }

    @Transactional
    public String startTask(String processInstanceId, String taskName, String actorName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);

        if (taskInstance == null)
            throw new IllegalStateException("Cannot start new task instance.");

        startTask(processInstanceFound, taskInstance);
        processInstanceRepository.save(processInstanceFound);

        return taskInstance.getId();
    }

    public void startTask(ProcessInstance processInstance, TaskInstance currentTask) {
        if (taskReady(processInstance, currentTask) == 1) {
            if (currentTask.getStateMachine().getState().equals("waiting"))
                currentTask.getStateMachine().unlock();
            else if (currentTask.getStateMachine().getState().equals("init"))
                currentTask.getStateMachine().evolve();
            currentTask.loadInputArtifact(processInstance.getArtifactInstanceList());
        } else if (taskReady(processInstance, currentTask) == 0)
            currentTask.getStateMachine().lock();
    }

    public Integer taskReady(ProcessInstance processInstance,TaskInstance currentTask) {
        List<Artifact> expectedInputList = currentTask.getTaskModel().getInput();
        for (Artifact expectedInput : expectedInputList) {
            if (!processInstance.checkExistenceArtifact(expectedInput))
                return 0;
        }
        // not allow duplicate task
        if (currentTask.getStateMachine().getState().equals("completed"))
            return 2;
        return 1;
    }

    @Transactional
    public void endTask(String processInstanceId, String taskId, List<PreDefinedArtifactInstance> preDefinedArtifactInstanceList) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.checkExistenceTaskInstance(taskId);

        if (taskInstance == null)
            throw new IllegalStateException("Task instance with id " + taskId + "does not exist.");

        endTask(processInstanceFound, taskInstance, preDefinedArtifactInstanceList);
        processInstanceRepository.save(processInstanceFound);
    }

    public void endTask(ProcessInstance processInstance, TaskInstance currentTask, List<PreDefinedArtifactInstance> preDefinedArtifactInstanceList) {
        if (currentTask.getStateMachine().getState().equals("inProgress")) {
            List<ArtifactInstance> processArtifactList = processInstance.getArtifactInstanceList();
            for (PreDefinedArtifactInstance preDefinedArtifactInstance : preDefinedArtifactInstanceList) {
                String name = preDefinedArtifactInstance.getName();
                String state = preDefinedArtifactInstance.getState();

                // check if the artifact and its state provided by the actor is the expected one of the process
                // if yes, continue
                // if no, print error so the actor can provide with a correct one
                if (!currentTask.verifyArtifactState(name, state)) {
                    System.out.println("Wrong output artifact or wrong expected state");
                    return;
                }

                // check if the output artifact name has been in the global list of the process
                // if yes, update the state
                // if no, add a new artifact to the global list
                boolean found = false;
                for (ArtifactInstance artifactInstance : processArtifactList) {
                    if (artifactInstance.getArtifact().getName().equals(name)) {
                        artifactInstance.getStateMachine().setState(state);
                        checkSharedArtifacts(processInstance, currentTask, found, artifactInstance, preDefinedArtifactInstance);
                        break;
                    }
                }

                if (!found) {
                    ArtifactInstance newArtifactInstance = currentTask.exportOutputArtifact(name, state, preDefinedArtifactInstance.getShared());
                    if (newArtifactInstance != null) {
                        if (preDefinedArtifactInstance.getShared() == 1)
                            updateArtifactLockStatus(processInstance, currentTask, newArtifactInstance, preDefinedArtifactInstance);

                        processInstance.addArtifactInstanceList(newArtifactInstance);
                    }
                }
            }
            currentTask.getStateMachine().evolve();
        }
    }

    private void checkSharedArtifacts(ProcessInstance processInstance, TaskInstance currentTask, boolean found, ArtifactInstance artifactInstance, PreDefinedArtifactInstance preDefinedArtifactInstance) {
        if (preDefinedArtifactInstance.getShared() == -1) {
            found = true;
        } else if (preDefinedArtifactInstance.getShared() == 0) {
            assert true;
        } else if (preDefinedArtifactInstance.getShared() == 1) {
            found = true;
            updateArtifactLockStatus(processInstance, currentTask, artifactInstance, preDefinedArtifactInstance);
        }
    }

    private void updateArtifactLockStatus(ProcessInstance processInstance, TaskInstance currentTask, ArtifactInstance artifactInstance, PreDefinedArtifactInstance preDefinedArtifactInstance) {
        artifactInstance.setShared(preDefinedArtifactInstance.getShared());
        if (processInstance.checkMultipleTaskInstanceNotCompleted(currentTask))
            artifactInstance.setLocked(false);
        else
            artifactInstance.setLocked(true);
    }

    public Integer validateTask(String processInstanceId, String taskName, String actorName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);

        if (taskInstance == null)
            return -1;

        return taskReady(processInstanceFound, taskInstance);
    }
}
