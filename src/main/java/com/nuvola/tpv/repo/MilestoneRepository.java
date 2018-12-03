package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Milestone;

public interface MilestoneRepository extends MongoRepository<Milestone, String> {
	
}
