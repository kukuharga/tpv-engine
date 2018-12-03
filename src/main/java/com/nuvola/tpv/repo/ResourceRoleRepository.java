package com.nuvola.tpv.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nuvola.tpv.model.ResourceRole;

public interface ResourceRoleRepository extends MongoRepository<ResourceRole, String> {

	List<ResourceRole> findByCodeIn(Collection<String> roles);

	ResourceRole findFirstByCodeAndLevel(String code, String level);
	@Query(value="{}")
	List<ResourceRole> findAll();
}
