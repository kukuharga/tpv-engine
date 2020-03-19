package com.nuvola.tpv.repo;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.TrainingSet;


public interface TrainingSetRepository extends MongoRepository<TrainingSet, String> {

	TrainingSet findFirstByProjectId(String projectId);
	
}
