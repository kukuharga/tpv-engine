package com.nuvola.tpv.controller;



import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import com.nuvola.tpv.model.Department;
import com.nuvola.tpv.model.DepartmentRevenue;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.InvoiceDistribution;
import com.nuvola.tpv.model.PurchaseOrderQueryStub;
import com.nuvola.tpv.model.ProjectType;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.repo.InvoiceRepository;
import com.nuvola.tpv.repo.PurchaseOrderRepository;
import com.nuvola.tpv.service.InvoiceService;
import com.nuvola.tpv.service.ProjectService;
import com.nuvola.tpv.service.PurchaseOrderService;

@RestController
@RequestMapping("/purchaseOrders")
public class PurchaseOrderController {
	private static Log log = LogFactory.getLog(PurchaseOrderController.class);

	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private PurchaseOrderRepository poRepository;

	@Autowired
	private PurchaseOrderService poService;
	
	@Autowired
	private ProjectService projectService;


	@GetMapping(value = "/combo")
	public Collection<PurchaseOrder> getAllPurchaseOrderMap() {
		return poRepository.findAllCombo();
	}

	@GetMapping(value = "/{poId}/invoices")
	public Collection<Invoice> getInvoicesByPo(@PathVariable(name = "poId") String poId) throws Exception {
		String poNumber = poService.getPONumberById(poId);
		if (poNumber == null) throw new NoSuchElementException("PO with ID == " + poId+" not found.");
		return invoiceRepository.findByPoNumber(poNumber);
	}
	
	@GetMapping(value = "/{poId}/invoices/{status}")
	public Collection<Invoice> getInvoicesByPoAndInvStatus(@PathVariable(name = "poId") String poId, @PathVariable(name="status") String status) throws Exception {
		log.info("poId=="+poId+".status=="+status);
		return invoiceService.getInvoiceByPoIdAndStatus(poId,status);
	}

	/**
	 * Get Department list whose revenue in a purchase order
	 * 
	 * @param poNumber
	 * @return
	 */
	@GetMapping(value = "/{poId}/departments")
	public Collection<Department> getDepartmentsByPo(@PathVariable(name = "poId") String poId) {

		return poService.getDepartmentByPoId(poId);
	}

	/**
	 * Get Invoice Amount by PO Payment Term
	 * 
	 * @param poId     
	 * @return
	 */
	@GetMapping(value = "/{poId}/terms/{termNo}")
	public Map<String,Double> getDefaultCosts(@PathVariable(name = "poId") String poId, @PathVariable("termNo") Integer term) {
		return poService.getPaymentAmountByPoIdAndTerm(poId, term);
	}
	
	@GetMapping(value = "/{poId}/terms")
	public Map<Integer,String> getPaymentTermsByPoId(@PathVariable(name = "poId") String poId) {
		return poService.getPaymentTermsByPoId(poId);
	}
	

	/**
	 * Get Project Types by Purchase Order ID and DeptCode
	 * 
	 * @param poId     
	 * @return
	 */
	@GetMapping(value = "/{poId}/departments/{deptCode}/projectTypes")
	public Collection<ProjectType> getProjectTypesByPurchaseOrderAndDepartment(@PathVariable(name = "poId") String poId, @PathVariable(name = "deptCode") String deptCode) {
		return poService.getProjectTypesByPoIdAndDeptCode(poId, deptCode);
	}
	
	@PostMapping(value="/search")
	public List<PurchaseOrder> findInvoice(@RequestBody PurchaseOrderQueryStub stub) {
		log.debug("search stub=="+stub);
		return poService.findPurchaseOrder(stub);
	}
	
	

	
	
	@GetMapping(value = "/download")
	public ResponseEntity<Resource> downloadGet(PurchaseOrderQueryStub stub) throws Exception {
//		public ResponseEntity<Resource> download() throws Exception {
		log.debug("Entering download. Search stub==" + stub);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"purchase_order.xls\"");

		ByteArrayResource resource = poService.downloadPurchaseOrder(stub);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
	}

	
	@GetMapping(value = "/download-full")
	public ResponseEntity<Resource> downloadFullGet(PurchaseOrderQueryStub stub) throws Exception {
		log.debug("Entering downloadFull..Search stub==" + stub);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"purchase_order_full.xls\"");

		ByteArrayResource resource = poService.downloadPurchaseOrderFull(stub);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
	}
	
	
	@GetMapping(value="/clients")
	public Collection<String> findAllClients() {
		return poService.getAllClients();
	}
	
	@GetMapping(value="/{poId}/invoiceDists/{term}")
	public Collection<InvoiceDistribution> getInvoiceDistByTerm(@PathVariable(name = "poId") String poId,@PathVariable(name = "term") Integer term){
		return poService.getInvoiceDist(poId,term);
		///purchaseOrders/{poId}/invoiceDists/{term}
	}
	
	@GetMapping(value="/{poId}/invoiceDists")
	public Collection<InvoiceDistribution> getInvoiceDist(@PathVariable(name = "poId") String poId){
		return poService.getInvoiceDist(poId);
		///purchaseOrders/{poId}/invoiceDists/{term}
	}
	
	@PostMapping(value="/{poId}/invoiceDists/{term}")
	public Collection<InvoiceDistribution> updateInvoiceDist(@PathVariable(name = "poId") String poId,@PathVariable(name = "term") Integer term,@RequestBody Collection<InvoiceDistribution> invoiceDists ){
		return poService.updateInvoiceDist(poId,term,invoiceDists);
		///purchaseOrders/{poId}/invoiceDists/{term}
	}
	
	@PostMapping(value="/revenues")
	public Collection<DepartmentRevenue> getDeptRevenue(@RequestBody PurchaseOrderQueryStub stub){
		if(stub == null || stub.getPoNumber() == null) return null;
		return projectService.getProjectDist(stub.getPoNumber());
				
	}

}