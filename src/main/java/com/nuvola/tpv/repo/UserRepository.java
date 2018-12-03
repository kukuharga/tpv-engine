package com.nuvola.tpv.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.User;

//@RepositoryRestResource(collectionResourceRel = "user", path = "users")
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    List<User>findByGroups(String groups);
    
    
   
    
}
