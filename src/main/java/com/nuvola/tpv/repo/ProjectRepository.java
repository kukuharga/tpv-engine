package com.nuvola.tpv.repo;



import java.util.Collection;
import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nuvola.tpv.model.Project;

//@PreAuthorize("hasRole('STANDARD_USER')")
public interface ProjectRepository extends MongoRepository<Project, String> {
	//List<Project> findBySales(@Param("userId")Long userId);
	//@PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
	List<Project> findBySales(@Param("username")String username);
	List<Project> findByServiceAndSalesIn(String service,Collection<String>users);
	List<Project> findBySalesIn(Collection<String>users);
	
	List<Project> findByLeadStage(String leadStage);
	
	List<Project> findByPoNumber(String poNumber);
	
//	Optional<Project> findById(String id);
	
	@Query(value="{}",fields="{ 'clientName' : 1 }")
	List<Project> findAllClients();
	
	List<Project> findBySalesAndProjectType(String sales,String projectType);
	

	
}
