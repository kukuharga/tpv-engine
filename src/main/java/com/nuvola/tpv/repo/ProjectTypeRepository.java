package com.nuvola.tpv.repo;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.ProjectType;


public interface ProjectTypeRepository extends MongoRepository<ProjectType, String> {

	
}
