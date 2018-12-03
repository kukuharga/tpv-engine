package com.nuvola.tpv.controller;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.PaymentQueryStub;
import com.nuvola.tpv.service.PaymentService;


@RestController
@RequestMapping("/payments")
public class PaymentController {
	private static final Log log = LogFactory.getLog(PaymentController.class);
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping(value="/search")
	public List<Payment> findPayment(@RequestBody PaymentQueryStub stub) {

		log.debug("query stub=="+stub);
		return paymentService.getPayment(stub);
	}
	
	@GetMapping(value="/download")
	public ResponseEntity<Resource> download(PaymentQueryStub stub) throws IOException {
		log.debug("search stub=="+stub);
		HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"payment.csv\"");
	   
	    ByteArrayResource resource = paymentService.downloadPayment(stub);

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(resource.contentLength())
	            .contentType(MediaType.parseMediaType("application/csv"))
	            .body(resource);
	}
	
	@GetMapping(value="/searchx")
	public List<Payment> findPaymentNew(@RequestBody PaymentQueryStub stub) {

		return paymentService.getPayment(stub);
	}
	

	



}