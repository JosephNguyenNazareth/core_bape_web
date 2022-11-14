package com.pms.core_bape_web.type.instance.process;

import com.pms.core_bape_web.casestudy.CaseStudyBape;
import com.pms.core_bape_web.casestudy.CaseStudyBapeImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessInstanceConfig {

    @Bean
    CommandLineRunner commandLineRunner(ProcessInstanceRepository processInstanceRepository) {
        return args -> {
            CaseStudyBape caseStudyBape = new CaseStudyBapeImpl("Shopping Website");
            caseStudyBape.loadProcess("TL1");

            ProcessInstance newProcessInstance = caseStudyBape.getProcessInstance();
            processInstanceRepository.save(newProcessInstance);
        };
    }
}
