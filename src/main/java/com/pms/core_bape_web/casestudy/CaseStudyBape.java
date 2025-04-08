package com.pms.core_bape_web.casestudy;

import com.pms.core_bape_web.type.instance.process.ProcessInstance;

public interface CaseStudyBape {
    ProcessInstance getProcessInstance();
    String getName();
    boolean checkProcessModelExistence();
    boolean loadProcessInstanceInfo(String processInstanceId);
    public void saveProcessInstanceInfo();
    void loadProcess(String creatorName);
}
