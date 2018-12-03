package com.nuvola.tpv.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.InvoiceQueryStub;
import com.nuvola.tpv.repo.InvoiceRepository;
import com.nuvola.tpv.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
	private static Log log = LogFactory.getLog(InvoiceController.class);

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceService invoiceService;



	@GetMapping(value = "/combo")
	public Map<String, String> getAllInvoiceMap() {
		return invoiceRepository.findAll().stream()
				.collect(Collectors.toMap(Invoice::getId, Invoice::getInvoiceString));
	}

	@PostMapping(value = "/search")
	public List<Invoice> findInvoice(@RequestBody InvoiceQueryStub stub) {
		log.debug("search stub==" + stub);
		return invoiceService.findInvoice(stub);
	}
	

//	@PostMapping(value = "/download")
//	public ResponseEntity<Resource> download(@RequestBody InvoiceQueryStub stub) throws IOException {
//		log.debug("search stub==" + stub);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//		headers.add("Pragma", "no-cache");
//		headers.add("Expires", "0");
//		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice.csv\"");
//
//		ByteArrayResource resource = invoiceService.downloadInvoice(stub);
//
//		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
//				.contentType(MediaType.parseMediaType("application/csv")).body(resource);
//	}

	@GetMapping(value = "/{invoiceId}/extraFields")
	public Invoice getInvoice(@PathVariable(name = "invoiceId") String invoiceId) {
		return invoiceService.getInvoice(invoiceId);
	}

	@PostMapping(value = "/{invoiceId}/outStandingAmt")
	public Map<String, Double> getInvoiceMinimumFields(@PathVariable(name = "invoiceId") String invoiceId,
			@RequestBody Map<String, Double> input) {
		Double paymentAmount = input.get("paymentAmount");
		return invoiceService.getOutstandingAmount(paymentAmount, invoiceId);
	}
	
	@GetMapping(value = "/download")
	public ResponseEntity<Resource> download(InvoiceQueryStub stub) throws Exception {
		log.debug("search stub=="+stub);
		HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice.csv\"");
	   
	    ByteArrayResource resource = invoiceService.downloadInvoice(stub);

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(resource.contentLength())
	            .contentType(MediaType.parseMediaType("application/csv"))
	            .body(resource);
	}
	

}