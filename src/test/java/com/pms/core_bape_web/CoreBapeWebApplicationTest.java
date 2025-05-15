package com.pms.core_bape_web;

import com.pms.core_bape_web.type.instance.process.ProcessInstance;
import com.pms.core_bape_web.type.instance.process.ProcessInstanceRepository;
import com.pms.core_bape_web.type.instance.process.ProcessInstanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CoreBapeWebApplicationTest {
    @Autowired
    ProcessInstanceService processInstanceService;

    @Autowired
    ProcessInstanceRepository processInstanceRepository;

    @Test
    public void createProcessInstance(){
        String p1Id = processInstanceService.addNewProcessInstance("P1","sunny");
        String p2Id = processInstanceService.addNewProcessInstance("P2","cherry");
        String p31Id = processInstanceService.addNewProcessInstance("P3","teddy");
        String p32Id = processInstanceService.addNewProcessInstance("P3","teddy");
    }

    @Test
    public void startProcessInstance(){
        ProcessInstance p1 = processInstanceService.getProcessInstance("72ff959c-fe23-48dd-84ac-ecf2e8e85839");
        p1.initTaskInstance(p1.getProcessModel().getTaskList());
        processInstanceRepository.save(p1);
    }
}