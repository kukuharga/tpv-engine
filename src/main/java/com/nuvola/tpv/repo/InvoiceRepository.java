package com.nuvola.tpv.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nuvola.tpv.model.Invoice;


public interface InvoiceRepository extends MongoRepository<Invoice, String> {
	
	@Query(value="{ 'poNumber' : ?0 , amount: { $gt: 800 } }",fields="{ 'invoiceDist' : 0}")
	public List<Invoice> findByPoNumber(String poNumber);
	
	@Query(fields="{ 'invoiceDist' : 0}")
	public List<Invoice> findByPoNumberAndStatus(String poNumber,String status);
	
	@Query(fields="{ 'invoiceDist' : 0}")
	public List<Invoice> findByStatus(String status);
	
	
	@Query(value="{}",fields="{ 'invoiceDist' : 0 }")
	List<Invoice> findAllCombo();

	public Invoice findFirstByInvNumber(String invNumber);
	
	
}
