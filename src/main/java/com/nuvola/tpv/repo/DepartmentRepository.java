package com.nuvola.tpv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.Department;


//@PreAuthorize("hasRole('STANDARD_USER')")
//@RepositoryRestResource
public interface DepartmentRepository extends MongoRepository<Department, String> {
//	public List<Department> find
	
}
