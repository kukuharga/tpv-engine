package com.nuvola.tpv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Activity;


public interface ActivityRepository extends MongoRepository<Activity, String> {
	List<Activity> findByGroup(String group);
}
