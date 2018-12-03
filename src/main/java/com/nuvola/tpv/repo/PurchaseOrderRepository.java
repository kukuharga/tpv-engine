package com.nuvola.tpv.repo;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.nuvola.tpv.model.PurchaseOrder;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
	@Query(value="{ 'poNumber' : ?0 }",fields="{ 'projectDist' : 1}")
	PurchaseOrder findFirstProjectDistByPoNumber(String poNumber);
	
	@Query(value="{ 'id' : ?0 }",fields="{ 'projectDist' : 1}")
	PurchaseOrder findProjectDistById(String id);
	
	@Query(value="{}",fields="{ 'projectDist' : 0, 'paymentTerms' : 0 }")
	List<PurchaseOrder> findAllCombo();
	
	@Query(value="{}",fields="{ 'client' : 1 }")
	List<PurchaseOrder> findAllClients();
	
	@Query(value="{ 'poNumber' : ?0 }",fields="{ 'paymentTerms' : 1}")
	PurchaseOrder findFirstPaymentTermsByPoNumber(String poNumber);
	
	@Query(value="{ 'id' : ?0 }",fields="{ 'paymentTerms' : 1}")
	PurchaseOrder findPaymentTermsById(String id);
	
	@Query(value="{ 'id' : ?0 }",fields="{ 'poNumber' : 1}")
	PurchaseOrder findPoNumberById(String id);
	
	List<PurchaseOrder> findByClient(String client);
	
	@Query(value="{ 'poNumber' : ?0 }",fields="{ 'projectDist' : 0, 'paymentTerms' : 0 }")
	PurchaseOrder findFirstByPoNumber2(String poNumber);
	
	@Query(value="{ 'id' : ?0 }",fields="{ 'invoiceDist' : 1}")
	PurchaseOrder findInvoiceDistById(String id);
	
	@Query(value="{ 'poNumber' : ?0 }",fields="{ 'invoiceDist' : 1}")
	PurchaseOrder findFirstInvoiceDistByPoNumber(String poNumber);

	List<PurchaseOrder> findByClientContainingIgnoreCase(String client);
	
	PurchaseOrder findFirstByPoNumber(String poNumber);

	
}
