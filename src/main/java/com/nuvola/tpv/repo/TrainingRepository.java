package com.nuvola.tpv.repo;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Training;


public interface TrainingRepository extends MongoRepository<Training, String> {

	Collection<Training> findByProjectId(String projectId);
	
}
