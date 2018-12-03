package com.nuvola.tpv.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.DefaultCostItem;
import com.nuvola.tpv.model.Installation;


public interface InstallationRepository extends MongoRepository<Installation, String> {

	Collection<Installation> findByProjectId(String projectId);
	
}
