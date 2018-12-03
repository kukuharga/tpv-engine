package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
}
