package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.casestudy.CaseStudyBape;
import com.pms.core_bape_web.casestudy.CaseStudyBapeImpl;
import com.pms.core_bape_web.type.instance.Actor;
import com.pms.core_bape_web.type.instance.ArtifactInstance;
import com.pms.core_bape_web.type.instance.PreDefinedArtifactInstance;
import com.pms.core_bape_web.type.instance.TaskInstance;
import com.pms.core_bape_web.type.model.Artifact;
import com.pms.core_bape_web.type.model.Task;
import com.pms.core_bape_web.utils.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessInstanceService {
    private final ProcessInstanceRepository processInstanceRepository;
    private Logging history;

    @Autowired
    public ProcessInstanceService(ProcessInstanceRepository processInstanceRepository) {
        this.processInstanceRepository = processInstanceRepository;
        this.history = new Logging();
    }

    public String getLog() {
        return this.history.loadLog();
    }

    public List<ProcessInstance> getProcessInstances() {
        return processInstanceRepository.findAll();
    }

    public List<ProcessInstance> getProcessInstanceByName(String processInstanceName) {
        List<ProcessInstance> processInstanceList = processInstanceRepository.findAll();
        List<ProcessInstance> returnedList = new ArrayList<>();

        for (ProcessInstance processInstance: processInstanceList) {
            if (processInstance.getProcessModel().getName().equals(processInstanceName))
                returnedList.add(processInstance);
        }

        return returnedList;
    }

    public ProcessInstance getProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        return processInstance;
    }
    public String addNewProcessInstance(String processName, String creatorName) {
        CaseStudyBape caseStudyBape = new CaseStudyBapeImpl(processName);
        if (caseStudyBape.getName() != null)
            caseStudyBape.loadProcess(creatorName);

        this.history.writeLog("Process instance " + caseStudyBape.getProcessInstance().getId() + " created.");
        processInstanceRepository.save(caseStudyBape.getProcessInstance());

        return caseStudyBape.getProcessInstance().getId();
    }


    @Transactional
    public void changeState(String processInstanceId, Boolean state) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        processInstanceFound.setProcessClosed(state);
        String processState = state ? "opened" : "closed";
        processInstanceFound.update("process " + processState);

        this.history.writeLog("Process instance " + processInstanceFound.getId() + " stage changed.");
        processInstanceRepository.save(processInstanceFound);
    }

    public List<String> getTask(String processInstanceId) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        List<Task> taskList = processInstanceFound.getProcessModel().getTaskList();
        List<String> taskNameList = new ArrayList<>();
        for (Task task : taskList) {
            taskNameList.add(task.getName());
        }

        return taskNameList;
    }

    public Task getTask(String processInstanceId, String taskName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        List<Task> taskList = processInstanceFound.getProcessModel().getTaskList();
        for (Task task : taskList) {
            if (task.getName().equals(taskName))
                return task;
        }

        return null;
    }

    @Transactional
    public String startTask(String processInstanceId, String taskName, String actorName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);

        if (taskInstance == null)
            throw new IllegalStateException("Cannot start new task instance.");

        this.history.writeLog("Task instance of " + taskName + "in process instance " + processInstanceFound.getId() + " started.");
        startTask(processInstanceFound, taskInstance);
        processInstanceRepository.save(processInstanceFound);

        return taskInstance.getId();
    }

    public void startTask(ProcessInstance processInstance, TaskInstance currentTask) {
        if (taskReady(processInstance, currentTask) == 1) {
            if (currentTask.getStateMachine().getState().equals("waiting"))
                currentTask.getStateMachine().unlock();
            else if (currentTask.getStateMachine().getState().equals("init")) {
                currentTask.getStateMachine().evolve();
                processInstance.update("startTask: " + currentTask.getTaskModel().getName() + ", id: " + currentTask.getId());
            }

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
    public String abortTask(String processInstanceId, String taskName, String actorName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);

        if (taskInstance == null)
            throw new IllegalStateException("Cannot abort given task instance.");

        this.history.writeLog("Task instance of " + taskName + "in process instance " + processInstanceFound.getId() + " aborted.");
        abortTask(processInstanceFound, taskInstance);
        processInstanceRepository.save(processInstanceFound);

        return taskInstance.getId();
    }

    public void abortTask(ProcessInstance processInstance, TaskInstance currentTask) {
        if (taskReady(processInstance, currentTask) == 1) {
            currentTask.getStateMachine().reset();
        } else if (taskReady(processInstance, currentTask) == 0)
            currentTask.getStateMachine().lock();
    }

    @Transactional
    public void endTask(String processInstanceId, String taskId, List<PreDefinedArtifactInstance> preDefinedArtifactInstanceList) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.checkExistenceTaskInstance(taskId);

        if (taskInstance == null)
            throw new IllegalStateException("Task instance with id " + taskId + "does not exist.");

        this.history.writeLog("Task instance of " + taskInstance.getTaskModel().getName() + "in process instance " + processInstanceFound.getId() + " completed.");
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
            processInstance.update("startTask: " + currentTask.getTaskModel().getName() + ", id: " + currentTask.getId());
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

//    public Integer validateTask(String processInstanceId, String taskName, String actorName) {
//        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));
//
//        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);
//
//        if (taskInstance == null)
//            return -1;
//
//        return taskReady(processInstanceFound, taskInstance);
//    }

    public TaskInstance validateTask(String processInstanceId, String taskName, String actorName) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));

        TaskInstance taskInstance = processInstanceFound.getTaskByName(taskName, actorName);

        if (taskInstance == null)
            return null;

        if (taskReady(processInstanceFound, taskInstance) == 1)
            return taskInstance;
        else
            return null;
    }

    public List<Artifact> getArtifact(String processInstanceId) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));
        List<Artifact> completeArtifactList = new ArrayList<>();
        for (Task task : processInstanceFound.getProcessModel().getTaskList()) {
            for (Artifact artifact: task.getInput()) {
                if (!completeArtifactList.contains(artifact))
                    completeArtifactList.add(artifact);
            }
        }

        return completeArtifactList;
    }

    public List<Actor> getActors(String processInstanceId) {
        ProcessInstance processInstanceFound = processInstanceRepository.findById(processInstanceId).orElseThrow(() -> new IllegalStateException("Process instance with id " + processInstanceId + "does not exist."));
        return processInstanceFound.getActorList();
    }
}
