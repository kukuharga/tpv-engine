package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Deliverable;

public interface DeliverableRepository extends MongoRepository<Deliverable, String> {
	
}
