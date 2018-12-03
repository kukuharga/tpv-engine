package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.InstallationPackage;


public interface InstallationPackageRepository extends MongoRepository<InstallationPackage, String> {
	
}
