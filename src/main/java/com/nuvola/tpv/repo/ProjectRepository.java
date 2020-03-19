package com.nuvola.tpv.repo;



import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nuvola.tpv.model.Project;

//@PreAuthorize("hasRole('STANDARD_USER')")
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
	//List<Project> findBySales(@Param("userId")Long userId);
	//@PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
	List<Project> findBySales(@Param("username")String username);
	List<Project> findByServiceAndSalesIn(String service,Collection<String>users,Sort sort);
	List<Project> findBySalesIn(Collection<String>users, Sort sort);
	List<Project> findBySalesInAndAssociatedProjectIdsIsEmptyAndSubProjectFalse(Collection<String>users, Sort sort);
	List<Project> findByLeadStage(String leadStage);	
	List<Project> findByPoNumber(String poNumber);
	
	
//	Optional<Project> findById(String id);
	
	@Query(value="{}",fields="{ 'clientName' : 1 }")
	List<Project> findAllClients();
	
	List<Project> findBySalesAndProjectType(String sales,String projectType);
	
//	@Query(value="{ $and: [ { 'reviews.reviewer' : ?0 }, { 'reviews.reviewerType' : ?1} ] }")
	@Query(value="{ 'reviews': { $elemMatch: { 'reviewer': ?0, 'reviewerType': ?1 } },'service': ?2 }")
	List<Project>findByReviewsAndLobType(String userName,String reviewerType,String lobType);
	
	@Query(value="{ 'reviews': { $elemMatch: { 'reviewer': ?0, 'reviewerType': ?1 } } }")
	List<Project> findByReviews(String userName, String reviewerType);

	
}
