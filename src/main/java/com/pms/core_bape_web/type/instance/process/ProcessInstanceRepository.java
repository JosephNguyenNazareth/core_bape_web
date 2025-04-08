package com.pms.core_bape_web.type.instance.process;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessInstanceRepository extends MongoRepository<ProcessInstance, String> {

    @Query(value="{name: '?0'}")
    List<ProcessInstance> findAllProcessInstancesByName(String processName);
}
