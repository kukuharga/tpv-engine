package com.nuvola.tpv.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.Payment;


public interface PaymentRepository extends MongoRepository<Payment, String> {

	List<Payment> findPaymentByPoNumber(String poNumber);
	List<Payment> findPaymentByPaymentDtBetween(Date start,Date finish);
	List<Payment> findPaymentByPoNumberAndPaymentDtBetween(String poNumber,Date start,Date finish);
	List<Payment> findByPoNumber(String poNumber);
	Payment findFirstByInvNumber(String invNumber);
	
	
}
