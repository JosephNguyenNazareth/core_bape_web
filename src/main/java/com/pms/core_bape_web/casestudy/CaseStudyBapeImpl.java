package com.pms.core_bape_web.casestudy;

import com.opencsv.CSVReader;
import com.pms.core_bape_web.type.instance.Actor;
import com.pms.core_bape_web.type.instance.process.ProcessInstance;
import com.pms.core_bape_web.type.model.Artifact;
import com.pms.core_bape_web.type.model.Process;
import com.pms.core_bape_web.type.model.Role;
import com.pms.core_bape_web.type.model.Task;
import com.pms.core_bape_web.utils.XMLParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CaseStudyBapeImpl implements CaseStudyBape {
    private String name;
    private String path;
    private ProcessInstance processInstance;

    public CaseStudyBapeImpl(String name) {
        File fileProcess = new File("./src/main/resources/static/" + name);

        if (!fileProcess.exists())
            throw new IllegalStateException("Process definition of " + name + " does not exist.");

        this.name = name;
        this.path = "./src/main/resources/static/" + this.name;
    }

    @Override
    public boolean checkProcessModelExistence() {
        File processPath = new File(this.path);
        return processPath.isDirectory();
    }

    @Override
    public void saveProcessInstanceInfo() {
        this.processInstance.saveProcessInstanceInfo();
    }

    @Override
    public boolean loadProcessInstanceInfo(String processInstanceId) {
        String instanceInfoPath =  "./src/main/resources/static" + this.name + "/processInfo/" + processInstanceId + ".json";
        File processInfoFile = new File(instanceInfoPath);
        if (!processInfoFile.exists()) {
            System.out.println("Process Instance is not existed");
            return false;
        }

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(instanceInfoPath));
            JSONObject jsonObject = (JSONObject) obj;
            this.processInstance = new ProcessInstance(jsonObject);
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public List<Actor> loadActor() {
        List<Actor> actorList = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(this.path + "/actor.csv");
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord = csvReader.readNext(); // header

            while ((nextRecord = csvReader.readNext()) != null) {
                Role role = new Role(nextRecord[1], Boolean.parseBoolean(nextRecord[2]));
                Actor actor = new Actor(nextRecord[0], role);
                actorList.add(actor);
            }

            return actorList;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return actorList;
    }

    @Override
    public void loadProcess(String creatorName) {
        File directoryPath = new File(this.path);
        String[] files = directoryPath.list();
        XMLParser xmlParser = new XMLParser();

        Process processModel = new Process(this.name);

        List<Artifact> globalInput = new ArrayList<>();
        List<Artifact> globalOutput = new ArrayList<>();

        List<Actor> actorList = loadActor();

        boolean correctCreator = false;
        for (Actor actor : actorList) {
            if (actor.getName().equals(creatorName)) {
                correctCreator = actor.getRole().isProjectManager();
            }
        }


        for(int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].contains(".xml")) {
                Document doc = xmlParser.generateXML(this.path + "/" + files[i]);
                String roleName = doc.getElementsByTagName("Role").item(0).getAttributes().getNamedItem("name").getNodeValue();
                String roleProjectManager = doc.getElementsByTagName("Role").item(0).getAttributes().getNamedItem("project_manager").getNodeValue();

                Role role = new Role(roleName, Boolean.parseBoolean(roleProjectManager));
                NodeList roleFragment = doc.getElementsByTagName("Task");

                loadTask(processModel, roleFragment, role, globalInput, globalOutput);
            }
        }

        if (!correctCreator) {
            System.out.println("Cannot create process instance. Only project manager can have the permission to do so.");
            return;
        }
        processInstance = new ProcessInstance(processModel, actorList);
        System.out.println(processInstance.getId());
//        saveProcessInstanceInfo();
    }

    private void loadArtifact(Task task, String type, Element artifactElement, List<Artifact> globalList) {
        Artifact artifact = new Artifact(artifactElement.getAttribute("name"), type, artifactElement.getAttribute("state"));
        if (type.equals("input"))
            task.addInput(artifact);
        else if (type.equals("output"))
            task.addOutput(artifact);

        if (!globalList.contains(artifact)) {
            globalList.add(artifact);
        }
    }

    private void loadTask(Process process, NodeList roleFragment, Role role, List<Artifact> globalInput, List<Artifact> globalOutput) {
        for (int j = 0; j < roleFragment.getLength(); j++) {
            Node taskNode = roleFragment.item(j);
            String taskName = taskNode.getAttributes().getNamedItem("name").getNodeValue();
            Task task = new Task(taskName, role);

            NodeList artifacts = taskNode.getChildNodes();
            for (int k = 0; k < artifacts.getLength(); k++) {
                Node artifactNode = artifacts.item(k);
                if (artifactNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element artifactElement = (Element) artifactNode;
                    if (artifactElement.getNodeName().equals("InputArtifact")) {
                        loadArtifact(task, "input", artifactElement, globalInput);
                    } else if (artifactElement.getNodeName().equals("OutputArtifact")) {
                        loadArtifact(task, "output", artifactElement, globalOutput);
                    }
                }
            }
            process.addTask(task);
        }
    }
}
