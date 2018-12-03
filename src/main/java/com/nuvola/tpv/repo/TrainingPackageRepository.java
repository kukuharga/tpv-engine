package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.TrainingPackage;


public interface TrainingPackageRepository extends MongoRepository<TrainingPackage, String> {
	
}
