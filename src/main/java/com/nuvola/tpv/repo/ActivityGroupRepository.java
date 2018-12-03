package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.ActivityGroup;

public interface ActivityGroupRepository extends MongoRepository<ActivityGroup, String> {
}
