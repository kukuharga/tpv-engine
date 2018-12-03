package com.nuvola.tpv.repo;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.OutOfPocket;

public interface OutOfPocketRepository extends MongoRepository<OutOfPocket, String> {
	
	@RestResource(rel="findByProjectId",path="findByProjectId")
	OutOfPocket findFirstByProjectId(String projectId);

	
	
}
