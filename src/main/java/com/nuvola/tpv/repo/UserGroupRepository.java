package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nuvola.tpv.model.UserGroup;

@RepositoryRestResource(collectionResourceRel = "groups", path = "groups")
public interface UserGroupRepository extends MongoRepository<UserGroup, String> {
    
   
    
}
