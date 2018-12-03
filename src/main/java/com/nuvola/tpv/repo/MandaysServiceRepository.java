package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;

import com.nuvola.tpv.model.MandaysService;

public interface MandaysServiceRepository extends MongoRepository<MandaysService, String> {
	
	@Query(value="{ 'projectId' : ?0 }",fields="{ 'deliverables' : 1}")
	MandaysService findDeliverablesByProjectId(String projectId);
	
	@Query(value="{ 'projectId' : ?0 }",fields="{ 'personnels' : 1}")
	MandaysService findPersonnelsByProjectId(String projectId);
	
	@RestResource(rel="findByProjectId",path="findByProjectId")
	MandaysService findFirstByProjectId(String projectId);
	
	
	
}
