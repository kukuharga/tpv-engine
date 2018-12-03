package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.ResourceLevel;


public interface ResourceLevelRepository extends MongoRepository<ResourceLevel, String> {
}
