package com.nuvola.tpv.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.nuvola.tpv.model.Department;
import com.nuvola.tpv.model.DepartmentRevenue;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.InvoiceDistribution;
import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.PaymentTerm;
import com.nuvola.tpv.model.ProjectType;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.model.PurchaseOrderQueryStub;
import com.nuvola.tpv.repo.DepartmentRepository;
import com.nuvola.tpv.repo.InvoiceRepository;
import com.nuvola.tpv.repo.PaymentRepository;
import com.nuvola.tpv.repo.ProjectTypeRepository;
import com.nuvola.tpv.repo.PurchaseOrderRepository;

@Component
public class PurchaseOrderService {
	private static Log log = LogFactory.getLog(PurchaseOrderService.class);
	@Autowired
	private PurchaseOrderRepository poRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private ProjectTypeRepository projectTypeRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Value("${csv.delimiter}")
	private String csvDelimiter;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	public Collection<Department> getDepartmentByPoId(String poId) {
		PurchaseOrder purchaseOrder = poRepository.findProjectDistById(poId);
		if (purchaseOrder == null || purchaseOrder.getProjectDist() == null)
			return null;
		Set<String> departments = purchaseOrder.getProjectDist().stream().map(n -> n.getDepartment())
				.collect(Collectors.toSet());
		List<Department> list = new ArrayList<Department>();
		departmentRepository.findAllById(departments).forEach(list::add);

		if (list.size() < departments.size()) {
			list = new ArrayList<Department>();
			for (String deptStr : departments) {
				list.add(new Department(deptStr, deptStr));
			}
		}
		return list;
	}

	public Collection<ProjectType> getProjectTypesByPoIdAndDeptCode(String poId, String deptCode) {
		PurchaseOrder purchaseOrder = poRepository.findProjectDistById(poId);
		if (purchaseOrder == null || purchaseOrder.getProjectDist() == null)
			return null;
		List<LobType> serviceTypes = purchaseOrder.getProjectDist().stream()
				.filter(n -> deptCode.equalsIgnoreCase(n.getDepartment())).map(n -> n.getService())
				.collect(Collectors.toList());
		List<ProjectType> list = new ArrayList<ProjectType>();
		projectTypeRepository.findAllById(serviceTypes).forEach(list::add);

		if (list.size() < serviceTypes.size()) {
			list = new ArrayList<ProjectType>();
			for (LobType svcTypeStr : serviceTypes) {
				list.add(new ProjectType(svcTypeStr, svcTypeStr.toString()));
			}
		}

		return list;
	}

	public Map<Integer, String> getPaymentTermsByPoId(String poId) {
		PurchaseOrder purchaseOrder = poRepository.findPaymentTermsById(poId);
		if (purchaseOrder == null || purchaseOrder.getPaymentTerms() == null)
			return null;

		Map<Integer, String> paymentTermMap = new TreeMap<Integer, String>();
		// Convert payment term list to a map of term and description.
		purchaseOrder.getPaymentTerms().forEach((k) -> paymentTermMap.put(k.getTerm(), k.getTermDesc()));
		return paymentTermMap;
	}

	/**
	 * This method is to get payment amount given po number and payment term
	 * 
	 * @param poId
	 * @param term
	 * @return
	 */
	public Map<String, Double> getPaymentAmountByPoIdAndTerm(String poId, int term) {
		PurchaseOrder purchaseOrder = poRepository.findPaymentTermsById(poId);
		if (purchaseOrder == null || purchaseOrder.getPaymentTerms() == null)
			return null;
		Collection<PaymentTerm> paymentTerms = purchaseOrder.getPaymentTerms();
		List<PaymentTerm> selectedTerm = paymentTerms.stream().filter(n -> n.getTerm() == term)
				.collect(Collectors.toList());
		if (selectedTerm == null || selectedTerm.isEmpty())
			return null;
		Map<String, Double> data = new HashMap<String, Double>();
		data.put("amount", selectedTerm.get(0).getAmount());
		return data;

	}

	/**
	 * Get PO Number by PO ID
	 * 
	 * @throws Exception
	 */
	public String getPONumberById(String poId) throws Exception {
		PurchaseOrder purchaseOrder = poRepository.findPoNumberById(poId);
		if (purchaseOrder == null)
			throw new Exception("PO with ID " + poId + " not found.");

		return purchaseOrder.getPoNumber();
	}

	public Collection<String> getAllClients() {
		List<PurchaseOrder> purchaseOrders = poRepository.findAllClients();
		List<String> clients = purchaseOrders.stream().filter(n -> n.getClient() != null).map(n -> n.getClient())
				.distinct().collect(Collectors.toList());
		Collections.sort(clients);
		return clients;

	}

	public Collection<InvoiceDistribution> getInvoiceDist(String poId) {
		PurchaseOrder purchaseOrder = poRepository.findInvoiceDistById(poId);
		if (purchaseOrder == null || purchaseOrder.getInvoiceDist() == null)
			return null;
		return purchaseOrder.getInvoiceDist().stream().sorted(new Comparator<InvoiceDistribution>() {
			@Override
			public int compare(InvoiceDistribution o1, InvoiceDistribution o2) {
				return o1.getTerm() - o2.getTerm();
			}
		}).collect(Collectors.toList());
	}

	public Collection<InvoiceDistribution> getInvoiceDist(String poId, Integer term) {
		return getInvoiceDist(poId).stream().filter(n -> n.getTerm() == term).collect(Collectors.toList());

	}

	public Collection<InvoiceDistribution> updateInvoiceDist(String poId, Integer term,
			Collection<InvoiceDistribution> invoiceDists) {
		PurchaseOrder purchaseOrder = poRepository.findById(poId).get();
		if (purchaseOrder == null)
			return null;
		if (purchaseOrder.getInvoiceDist() == null)
			purchaseOrder.setInvoiceDist(new ArrayList<InvoiceDistribution>());

		Collection<InvoiceDistribution> invoiceDistOri = purchaseOrder.getInvoiceDist();

		for (Iterator<InvoiceDistribution> iterator = invoiceDistOri.iterator(); iterator.hasNext();) {
			InvoiceDistribution invoiceDist = iterator.next();
			if (invoiceDist.getTerm() == term)
				iterator.remove();
		}

		invoiceDistOri.addAll(invoiceDists);
		return poRepository.save(purchaseOrder).getInvoiceDist().stream().filter(n -> n.getTerm() == term)
				.collect(Collectors.toList());

	}

	public List<PurchaseOrder> findPurchaseOrder(PurchaseOrderQueryStub queryStub) {

		Query query = new Query();

		if (!StringUtils.isEmpty(queryStub.getPoNumber())) {
			query.addCriteria(Criteria.where("poNumber").is(queryStub.getPoNumber()));
		}

		if (!StringUtils.isEmpty(queryStub.getClient())) {
			query.addCriteria(Criteria.where("client").regex(queryStub.getClient(), "i"));
		}
		
		if (queryStub.getStartDate() != null && queryStub.getFinishDate() != null) {
			query.addCriteria(Criteria.where("poDate").gte(queryStub.getStartDate()).lte(queryStub.getFinishDate()));
		}else if (queryStub.getStartDate() != null ) {
			query.addCriteria(Criteria.where("poDate").gte(queryStub.getStartDate()));
		}else if (queryStub.getFinishDate() != null ) {
			query.addCriteria(Criteria.where("poDate").lte(queryStub.getFinishDate()));
		}
		

		return mongoTemplate.find(query, PurchaseOrder.class);

	}

	public ByteArrayResource downloadPurchaseOrder(PurchaseOrderQueryStub queryStub) throws Exception {
		log.debug("Invoking downloadPurchaseOrder..");
		return buildExcelDocument(queryStub);
	}
	
	public ByteArrayResource downloadPurchaseOrderFull(PurchaseOrderQueryStub queryStub) throws Exception {
		log.debug("Invoking downloadPurchaseOrderFull..");
		return buildExcelDocument2(queryStub);
	}

	protected ByteArrayResource buildExcelDocument(PurchaseOrderQueryStub queryStub) throws Exception {

		// Get data
		List<PurchaseOrder> orders = findPurchaseOrder(queryStub);
		// create excel xls sheet
		Workbook workbook = new HSSFWorkbook();
		generateReportGroupContent(orders, workbook);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		workbook.write(stream);
		workbook.close();
		return new ByteArrayResource(stream.toByteArray());
	}

	protected ByteArrayResource buildExcelDocument2(PurchaseOrderQueryStub queryStub) throws Exception {

		// Get data
		List<PurchaseOrder> orders = findPurchaseOrder(queryStub);
		// create excel xls sheet
		Workbook workbook = new HSSFWorkbook();
		generateCollectionReportContent(orders, workbook);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		workbook.write(stream);
		workbook.close();
		return new ByteArrayResource(stream.toByteArray());
	}

	private void generateCollectionReportContent(List<PurchaseOrder> orders, Workbook workbook) {
		int rowCount = 1;

		CellStyle headerStyle = getHeaderStyle(workbook);
		Sheet sheet = createWorksheet(workbook, "Invoice Divisi & Mandays");
		generateCollectionReportHeader(sheet, headerStyle);

		// Default blank values
		Collection<PaymentTerm> defPaymentTerms = Arrays.asList(new PaymentTerm());
		Collection<InvoiceDistribution> defInvoiceDist = Arrays.asList(new InvoiceDistribution());
		Collection<DepartmentRevenue> defDeptRevenues = Arrays.asList(new DepartmentRevenue());
		Collection<Invoice> defInvoices = Arrays.asList(new Invoice());
		Payment payment = null;

		for (PurchaseOrder order : orders) {

			Collection<InvoiceDistribution> invoiceDist = orElse(order.getInvoiceDist(), defInvoiceDist);
			Collection<PaymentTerm> paymentTerms = orElse(order.getPaymentTerms(), defPaymentTerms);
			Collection<DepartmentRevenue> deptRevenues = orElse(order.getProjectDist(), defDeptRevenues);
			Collection<Invoice> invoices = orElse(invoiceRepository.findByPoNumber(order.getPoNumber()), defInvoices);

			for (InvoiceDistribution inv : invoiceDist) {
				Optional<PaymentTerm> termOpt = paymentTerms.stream().filter(n -> n.getTerm() == inv.getTerm())
						.findFirst();
				PaymentTerm term = termOpt.orElse(null);

				Optional<DepartmentRevenue> revOpt = deptRevenues.stream()
						.filter(n -> n.getDepartment().equals(inv.getDepartment())).findFirst();
				DepartmentRevenue rev = revOpt.orElse(null);

				Optional<Invoice> invOpt = invoices.stream().filter(n -> n.getTerm() == inv.getTerm()).findFirst();
				Invoice invoice = invOpt.orElse(null);
				payment = (invoice != null) ? paymentRepository.findFirstByInvNumber(invoice.getInvNumber()) : null;
				Row row = sheet.createRow(rowCount++);
				term.setInvoiceStatus(invoice != null ? "BILLED" : "UNBILLED");

				generateCollectionSheetContent(row, order, inv, invoice, rev, payment, term);

			}


		}

	}

	private void generateCollectionReportHeader(Sheet sheet, CellStyle style) {
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("PO Date");
		header.getCell(0).setCellStyle(style);
		header.createCell(1).setCellValue("Divisi");
		header.getCell(1).setCellStyle(style);
		header.createCell(2).setCellValue("Tgl Kontrak");
		header.getCell(2).setCellStyle(style);
		header.createCell(3).setCellValue("No Kontrak");
		header.getCell(3).setCellStyle(style);
		header.createCell(4).setCellValue("PO Hardcopy");
		header.getCell(4).setCellStyle(style);
		header.createCell(5).setCellValue("PO Number");
		header.getCell(5).setCellStyle(style);
		header.createCell(6).setCellValue("Charge Code");
		header.getCell(6).setCellStyle(style);
		header.createCell(7).setCellValue("Presales");
		header.getCell(7).setCellStyle(style);
		header.createCell(8).setCellValue("Sales");
		header.getCell(8).setCellStyle(style);
		header.createCell(9).setCellValue("Client");
		header.getCell(9).setCellStyle(style);
		header.createCell(10).setCellValue("Name of Project");
		header.getCell(10).setCellStyle(style);
		header.createCell(11).setCellValue("Project Description");
		header.getCell(11).setCellStyle(style);
		header.createCell(12).setCellValue("Type");
		header.getCell(12).setCellStyle(style);
		header.createCell(13).setCellValue("PO Curr");
		header.getCell(13).setCellStyle(style);
		header.createCell(14).setCellValue("PO Amount");
		header.getCell(14).setCellStyle(style);
		header.createCell(15).setCellValue("Term");
		header.getCell(15).setCellStyle(style);
		header.createCell(16).setCellValue("Term of Payment");
		header.getCell(16).setCellStyle(style);
		header.createCell(17).setCellValue("Revenue Amount");
		header.getCell(17).setCellStyle(style);
		header.createCell(18).setCellValue("INV Number");
		header.getCell(18).setCellStyle(style);
		header.createCell(19).setCellValue("Invoice Date");
		header.getCell(19).setCellStyle(style);
		header.createCell(20).setCellValue("Gross Invoice");
		header.getCell(20).setCellStyle(style);
		header.createCell(21).setCellValue("PPh 23");
		header.getCell(21).setCellStyle(style);
		header.createCell(22).setCellValue("PPN");
		header.getCell(22).setCellStyle(style);
		header.createCell(23).setCellValue("Invoicing Status");
		header.getCell(23).setCellStyle(style);
		header.createCell(24).setCellValue("Invoice Prediction");
		header.getCell(24).setCellStyle(style);
		header.createCell(25).setCellValue("Payment Status");
		header.getCell(25).setCellStyle(style);
		header.createCell(26).setCellValue("Payment Prediction");
		header.getCell(26).setCellStyle(style);
		header.createCell(27).setCellValue("Payment Date");
		header.getCell(27).setCellStyle(style);
		header.createCell(28).setCellValue("Payment Amount");
		header.getCell(28).setCellStyle(style);
		header.createCell(29).setCellValue("Payment PPN");
		header.getCell(29).setCellStyle(style);
		header.createCell(30).setCellValue("Outstanding Amount");
		header.getCell(30).setCellStyle(style);
		header.createCell(31).setCellValue("Outstanding Notes");
		header.getCell(31).setCellStyle(style);
		

		
	}

	private void generateCollectionSheetContent(Row row, PurchaseOrder order, InvoiceDistribution inv, Invoice invoice,
			DepartmentRevenue rev, Payment payment, PaymentTerm term) {
		Workbook workbook = row.getSheet().getWorkbook();
		CellStyle dateStyle = getDateStyle(workbook);
		CellStyle amountStyle = getAmountStyle(workbook);
		row.createCell(0).setCellValue(order.getPoDate());//PO Date
		row.getCell(0).setCellStyle(dateStyle);
		row.createCell(1).setCellValue(inv.getDepartment());//Divisi
		
		if (order.isContractExist()) {
			row.createCell(2).setCellValue(order.getContractDate());//Tgl Kontrak
			row.getCell(2).setCellStyle(dateStyle);
			row.createCell(3).setCellValue(order.getContractNo());//No Kontrak
		}
		row.createCell(4).setCellValue(order.isPoHardcopy());//PO Hardcopy
		row.createCell(5).setCellValue(order.getPoNumber());
		row.createCell(6).setCellValue(rev.getChargeCode());
		row.createCell(7).setCellValue(rev.getPresalesPerson());
		row.createCell(8).setCellValue(rev.getSalesPerson());
		row.createCell(9).setCellValue(order.getClient());
		row.createCell(10).setCellValue(order.getPoDesc());//Name of Project
		row.createCell(11).setCellValue(rev.getProjectNm());//Project Description
		row.createCell(12).setCellValue(rev.getService().toString());//Type
		row.createCell(13).setCellValue(order.getPoCurrency());//PO Currency
		row.createCell(14).setCellValue(order.getPoValue());
		row.getCell(14).setCellStyle(amountStyle);
		row.createCell(15).setCellValue(inv.getTerm());//Term
		row.createCell(16).setCellValue(term.getTermDesc());//Term of Payment
		row.createCell(17).setCellValue(inv.getAmount());// Revenue Amount
		row.getCell(17).setCellStyle(amountStyle);
		if (invoice != null) {
			row.createCell(18).setCellValue(invoice.getInvNumber());// INV Number
			row.createCell(19).setCellValue(invoice.getInvDate());// Invoice Date
			row.getCell(19).setCellStyle(dateStyle);
			row.createCell(20).setCellValue(invoice.getAmount());// Gross Invoice
			row.getCell(20).setCellStyle(amountStyle);
			row.createCell(21).setCellValue(invoice.getIncomeTax());// PPh 23
			row.getCell(21).setCellStyle(amountStyle);
			row.createCell(22).setCellValue(invoice.getVat());// PPN
			row.getCell(22).setCellStyle(amountStyle);
			row.createCell(25).setCellValue(invoice.getStatus());
			row.createCell(26).setCellValue(invoice.getPaymentPrediction());//Payment Prediction
		}
		row.createCell(23).setCellValue(term.getInvoiceStatus());// Invoicing Status
		row.createCell(24).setCellValue(term.getInvPredictionDt());// Invoice Prediction
		row.getCell(24).setCellStyle(dateStyle);
		if (payment != null) {
			row.createCell(27).setCellValue(payment.getPaymentDt());
			row.getCell(27).setCellStyle(dateStyle);
			row.createCell(28).setCellValue(payment.getPaymentAmt());
			row.getCell(28).setCellStyle(amountStyle);
			row.createCell(29).setCellValue(payment.getVat());
			row.getCell(29).setCellStyle(amountStyle);
			row.createCell(30).setCellValue(payment.getOutstandingAmt());
			row.getCell(30).setCellStyle(amountStyle);
			row.createCell(31).setCellValue(payment.getOutstandingNotes());
		}

	}

	private CellStyle getAmountStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 4); // "#,##0.00"
		return style;
	}

	// private boolean isEmpty(Collection<?> collections) {
	// return (collections == null || collections.isEmpty());
	// }

	private CellStyle getDateStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 15); // 0xf, "d-mmm-yy"
		return style;
	}

	private <T> Collection<T> orElse(Collection<T> dataList1, Collection<T> dataList2) {
		return (dataList1 == null) ? dataList2 : dataList1;
	}

	// private <T> T orElse(T object1, T object2) {
	// return (object1 == null) ? object2 : object1;
	// }

	private Sheet createWorksheet(Workbook workbook, String sheetName) {
		Sheet sheet = workbook.createSheet(sheetName);
		sheet.setDefaultColumnWidth(30);
		return sheet;
	}

	/**
	 * Generate Revenue and Backlog Report
	 * 
	 * @param orders
	 * @param workbook
	 */
	private void generateReportGroupContent(List<PurchaseOrder> orders, Workbook workbook) {
		int rowCount1 = 1, rowCount2 = 1, rowCount3 = 1;

		CellStyle headerStyle = getHeaderStyle(workbook);
		Sheet revenueSheet = createWorksheet(workbook, "Revenue Report");
		generateRevenueReportHeader(revenueSheet, headerStyle);
		Sheet backLogSheet = createWorksheet(workbook, "Backlog - Report");
		generateBackLogReportHeader(backLogSheet, headerStyle);
		Sheet arSheet = createWorksheet(workbook, "AR Report");
		generateARReportHeader(arSheet, headerStyle);
		Sheet poSheet = createWorksheet(workbook, "PO - Report");
		generatePOReportHeader(poSheet, headerStyle);
		
		// Default blank values
		Collection<PaymentTerm> defPaymentTerms = Arrays.asList(new PaymentTerm());
		Collection<InvoiceDistribution> defInvoiceDist = Arrays.asList(new InvoiceDistribution());
		Collection<DepartmentRevenue> defDeptRevenues = Arrays.asList(new DepartmentRevenue());
		Collection<Invoice> defInvoices = Arrays.asList(new Invoice());

		for (PurchaseOrder order : orders) {

			Collection<InvoiceDistribution> invoiceDist = orElse(order.getInvoiceDist(), defInvoiceDist);
			Collection<PaymentTerm> paymentTerms = orElse(order.getPaymentTerms(), defPaymentTerms);
			Collection<DepartmentRevenue> deptRevenues = orElse(order.getProjectDist(), defDeptRevenues);
			Collection<Invoice> invoices = orElse(invoiceRepository.findByPoNumber(order.getPoNumber()), defInvoices);

			for (InvoiceDistribution inv : invoiceDist) {
				Optional<PaymentTerm> termOpt = paymentTerms.stream().filter(n -> n.getTerm() == inv.getTerm())
						.findFirst();
				PaymentTerm term = termOpt.orElse(null);

				Optional<DepartmentRevenue> revOpt = deptRevenues.stream()
						.filter(n -> n.getDepartment().equals(inv.getDepartment())).findFirst();
				DepartmentRevenue rev = revOpt.orElse(null);

				Optional<Invoice> invOpt = invoices.stream().filter(n -> n.getTerm() == inv.getTerm()).findFirst();
				Invoice invoice = invOpt.orElse(null);

				Row backLogRow = backLogSheet.createRow(rowCount1);
				Row revenueRow = revenueSheet.createRow(rowCount1);
				term.setInvoiceStatus(invoice != null ? "BILLED" : "UNBILLED");

				generateBackLogSheetContent(backLogRow, order, term, inv, rev);
				generateRevenueSheetContent(revenueRow, order, inv, invoice);

				rowCount1++;
			}

			for (PaymentTerm term : paymentTerms) {
				Optional<Invoice> invOpt = invoices.stream().filter(n -> n.getTerm() == term.getTerm()).findFirst();
				Invoice invoice = invOpt.orElse(null);
				Row arRow = arSheet.createRow(rowCount2++);
				generateARSheetContent(arRow, order, term, invoice);
			}

			for (DepartmentRevenue rev : deptRevenues) {
				Row userRow = poSheet.createRow(rowCount3++);
				generatePoSheetContent(userRow, order, rev);
			}

		}
	}

	private void generatePoSheetContent(Row row, PurchaseOrder order, DepartmentRevenue rev) {
		Workbook workbook = row.getSheet().getWorkbook();
		CellStyle dateStyle = getDateStyle(workbook);
		CellStyle amountStyle = getAmountStyle(workbook);
		row.createCell(0).setCellValue(order.getPoDate());
		row.getCell(0).setCellStyle(dateStyle);
		row.createCell(1).setCellValue(order.getPoNumber());
		row.createCell(2).setCellValue(order.getClient());
		row.createCell(3).setCellValue(order.getPoDesc());
		row.createCell(4).setCellValue(order.getPoValue());
		row.getCell(4).setCellStyle(amountStyle);
		row.createCell(5).setCellValue(rev.getDepartment());
		row.createCell(6).setCellValue(rev.getChargeCode());
		row.createCell(7).setCellValue(rev.getRevenue());
		row.getCell(7).setCellStyle(amountStyle);

	}

	private void generateBackLogSheetContent(Row backLogRow, PurchaseOrder order, PaymentTerm term,
			InvoiceDistribution inv, DepartmentRevenue rev) {
		Workbook workbook = backLogRow.getSheet().getWorkbook();
		CellStyle dateStyle = getDateStyle(workbook);
		CellStyle amountStyle = getAmountStyle(workbook);
		backLogRow.createCell(0).setCellValue(term.getInvoiceStatus());
		backLogRow.createCell(1).setCellValue(order.getClient());
		backLogRow.createCell(2).setCellValue(order.getPoNumber());
		backLogRow.createCell(3).setCellValue(order.getPoDate());
		backLogRow.getCell(3).setCellStyle(dateStyle);
		backLogRow.createCell(4).setCellValue(order.getPoDesc());
		backLogRow.createCell(5).setCellValue(order.getPoValue());
		backLogRow.getCell(5).setCellStyle(amountStyle);
		backLogRow.createCell(6).setCellValue(inv.getDepartment());
		backLogRow.createCell(7).setCellValue(rev.getChargeCode());
		backLogRow.createCell(8).setCellValue(inv.getTerm());
		backLogRow.createCell(9).setCellValue(term.getTermDesc());
		backLogRow.createCell(10).setCellValue(inv.getAmount());
		backLogRow.getCell(10).setCellStyle(amountStyle);
		backLogRow.createCell(11).setCellValue(term.getInvPredictionDt());
		backLogRow.getCell(11).setCellStyle(dateStyle);
		backLogRow.createCell(12).setCellValue(inv.getAmount());
		backLogRow.getCell(12).setCellStyle(amountStyle);

	}

	private void generateBackLogReportHeader(Sheet sheet, CellStyle style) {
		// create header row - AR - Report
		// create header row - PO - Report
		Row header2 = sheet.createRow(0);
		header2.createCell(0).setCellValue("Invoicing Status");
		header2.getCell(0).setCellStyle(style);
		header2.createCell(1).setCellValue("Client");
		header2.getCell(1).setCellStyle(style);
		header2.createCell(2).setCellValue("PO Number");
		header2.getCell(2).setCellStyle(style);
		header2.createCell(3).setCellValue("PO Date");
		header2.getCell(3).setCellStyle(style);
		header2.createCell(4).setCellValue("Project Name");
		header2.getCell(4).setCellStyle(style);
		header2.createCell(5).setCellValue("PO Amount");
		header2.getCell(5).setCellStyle(style);
		header2.createCell(6).setCellValue("Divisi");
		header2.getCell(6).setCellStyle(style);
		header2.createCell(7).setCellValue("Chargecode");
		header2.getCell(7).setCellStyle(style);
		header2.createCell(8).setCellValue("Term");
		header2.getCell(8).setCellStyle(style);
		header2.createCell(9).setCellValue("Term of Payment");
		header2.getCell(9).setCellStyle(style);
		header2.createCell(10).setCellValue("Revenue Amount");
		header2.getCell(10).setCellStyle(style);
		header2.createCell(11).setCellValue("Invoice Prediction");
		header2.getCell(11).setCellStyle(style);
		header2.createCell(12).setCellValue("Amount");
		header2.getCell(12).setCellStyle(style);

	}

	private void generateRevenueSheetContent(Row revenueRow, PurchaseOrder order, InvoiceDistribution inv,
			Invoice invoice) {
		Workbook workbook = revenueRow.getSheet().getWorkbook();
		CellStyle dateStyle = getDateStyle(workbook);
		CellStyle amountStyle = getAmountStyle(workbook);

		if (invoice != null) {
			revenueRow.createCell(7).setCellValue(invoice.getInvNumber());
			revenueRow.createCell(1).setCellValue(invoice.getInvDate());
			revenueRow.getCell(1).setCellStyle(dateStyle);
			revenueRow.createCell(0).setCellValue("BILLED");
		} else {
			revenueRow.createCell(0).setCellValue("UNBILLED");
		}

		revenueRow.createCell(2).setCellValue(inv.getDepartment());
		revenueRow.createCell(3).setCellValue(inv.getService());
		revenueRow.createCell(4).setCellValue(order.getClient());
		revenueRow.createCell(5).setCellValue(order.getPoDesc());
		revenueRow.createCell(6).setCellValue(order.getPoValue());
		revenueRow.getCell(6).setCellStyle(amountStyle);

		revenueRow.createCell(8).setCellValue(inv.getAmount());
		revenueRow.getCell(8).setCellStyle(amountStyle);

	}

	private void generateARSheetContent(Row arRow, PurchaseOrder order, PaymentTerm term, Invoice invoice) {
		Workbook workbook = arRow.getSheet().getWorkbook();
		CellStyle dateStyle = getDateStyle(workbook);
		CellStyle amountStyle = getAmountStyle(workbook);

		arRow.createCell(1).setCellValue(term.getInvoiceStatus());

		if (invoice != null) {
			arRow.createCell(0).setCellValue(invoice.getStatus());
			arRow.createCell(1).setCellValue("BILLED");
			arRow.createCell(5).setCellValue(invoice.getInvDate());
			arRow.getCell(5).setCellStyle(dateStyle);
			arRow.createCell(6).setCellValue(invoice.getInvNumber());
		} else {
			arRow.createCell(0).setCellValue("UNPAID");
		}

		arRow.createCell(2).setCellValue(order.getClient());
		arRow.createCell(3).setCellValue(order.getPoDesc());
		arRow.createCell(4).setCellValue(order.getPoValue());
		arRow.getCell(4).setCellStyle(amountStyle);
		arRow.createCell(7).setCellValue(term.getAmount());
		arRow.getCell(7).setCellStyle(amountStyle);
	}

	private void generateARReportHeader(Sheet sheet, CellStyle defaultStyle) {
		// create header row - AR - Report
		Row header1 = sheet.createRow(0);
		header1.createCell(0).setCellValue("Payment Status");
		header1.getCell(0).setCellStyle(defaultStyle);
		header1.createCell(1).setCellValue("Invoicing Status");
		header1.getCell(1).setCellStyle(defaultStyle);
		header1.createCell(2).setCellValue("Client");
		header1.getCell(2).setCellStyle(defaultStyle);
		header1.createCell(3).setCellValue("Name of Project");
		header1.getCell(3).setCellStyle(defaultStyle);
		header1.createCell(4).setCellValue("PO Amount");
		header1.getCell(4).setCellStyle(defaultStyle);
		header1.createCell(5).setCellValue("Invoice Date");
		header1.getCell(5).setCellStyle(defaultStyle);
		header1.createCell(6).setCellValue("INV Number");
		header1.getCell(6).setCellStyle(defaultStyle);
		header1.createCell(7).setCellValue("Amount");
		header1.getCell(7).setCellStyle(defaultStyle);

	}

	private void generateRevenueReportHeader(Sheet sheet, CellStyle defaultStyle) {
		// create header row - Revenue - Report
		Row header1 = sheet.createRow(0);
		header1.createCell(0).setCellValue("Invoicing Status");
		header1.getCell(0).setCellStyle(defaultStyle);
		header1.createCell(1).setCellValue("Invoice Date");
		header1.getCell(1).setCellStyle(defaultStyle);
		header1.createCell(2).setCellValue("Divisi");
		header1.getCell(2).setCellStyle(defaultStyle);
		header1.createCell(3).setCellValue("Type");
		header1.getCell(3).setCellStyle(defaultStyle);
		header1.createCell(4).setCellValue("Client");
		header1.getCell(4).setCellStyle(defaultStyle);
		header1.createCell(5).setCellValue("Name of Project");
		header1.getCell(5).setCellStyle(defaultStyle);
		header1.createCell(6).setCellValue("PO Amount");
		header1.getCell(6).setCellStyle(defaultStyle);
		header1.createCell(7).setCellValue("INV Number");
		header1.getCell(7).setCellStyle(defaultStyle);
		header1.createCell(8).setCellValue("Revenue Amount");
		header1.getCell(8).setCellStyle(defaultStyle);
	}

	private void generatePOReportHeader(Sheet sheet, CellStyle style) {
		// create header row - PO - Report
		Row header1 = sheet.createRow(0);
		header1.createCell(0).setCellValue("PO Date");
		header1.getCell(0).setCellStyle(style);
		header1.createCell(1).setCellValue("PO Number");
		header1.getCell(1).setCellStyle(style);
		header1.createCell(2).setCellValue("Client");
		header1.getCell(2).setCellStyle(style);
		header1.createCell(3).setCellValue("Project Name");
		header1.getCell(3).setCellStyle(style);
		header1.createCell(4).setCellValue("PO Amount");
		header1.getCell(4).setCellStyle(style);
		header1.createCell(5).setCellValue("Divisi");
		header1.getCell(5).setCellStyle(style);
		header1.createCell(6).setCellValue("Chargecode");
		header1.getCell(6).setCellStyle(style);
		header1.createCell(7).setCellValue("Revenue Amount");
		header1.getCell(7).setCellStyle(style);
	}

	private CellStyle getHeaderStyle(Workbook workbook) {
		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		return style;
	}

}
