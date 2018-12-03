package com.nuvola.tpv.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.Purchase;


public interface PurchaseRepository extends MongoRepository<Purchase, String> {

	public List<Purchase>findByProjectId(String projectId);
	
	public void deleteByProjectId(String projectId);


	
}
