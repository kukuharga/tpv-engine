package com.nuvola.tpv.service;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.InvoiceDistribution;
import com.nuvola.tpv.model.InvoiceQueryStub;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.PaymentTerm;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.repo.InvoiceRepository;
import com.nuvola.tpv.repo.PurchaseOrderRepository;

@Component
public class InvoiceService {
	private static Log log = LogFactory.getLog(InvoiceService.class);
	@Autowired
	private InvoiceRepository invoiceRepository;
	@Autowired
	private PurchaseOrderRepository poRepository;
	@Autowired
	private PurchaseOrderService poService;
	@Autowired
	MongoTemplate mongoTemplate;
	@Value("${csv.delimiter}")
	private String csvDelimiter;

	public Invoice getInvoice(String id) {
		log.info("invoice id: " + id);
		Invoice invoice = invoiceRepository.findById(id).get();
		PurchaseOrder purchaseOrder = poRepository.findFirstByPoNumber2(invoice.getPoNumber());

		if (purchaseOrder == null)
			return invoice;

		invoice.setPoId(purchaseOrder.getId());
		invoice.setPoCurrency(purchaseOrder.getPoCurrency());
		invoice.setPoDesc(purchaseOrder.getPoDesc());

		return invoice;

	}

	public Map<String, Double> getOutstandingAmount(Double paymentAmount, String invoiceId) {
		Invoice invoice = invoiceRepository.findById(invoiceId).get();
		// 25300000 - 24840000 +460000
		Double outstandingAmt = (invoice != null) ? invoice.getTotalInvoice() - paymentAmount - invoice.getIncomeTax()
				: 0;
		Map<String, Double> result = new HashMap<String, Double>();
		result.put("outstandingAmt", outstandingAmt);
		return result;

	}

	public Collection<Invoice> getInvoiceByPoIdAndStatus(String poId, String invStatus) throws Exception {
		String poNumber = poService.getPONumberById(poId);

		if (StringUtils.isEmpty(poNumber))
			throw new Exception("PO Number empty for PO with ID == " + poId);
		log.info("poNumber==" + poNumber);
		return invoiceRepository.findByPoNumberAndStatus(poNumber, invStatus);

	}

	public List<Invoice> findInvoice(InvoiceQueryStub queryStub) {
		Query query = new Query();

		if (!StringUtils.isEmpty(queryStub.getInvNumber())) {
			query.addCriteria(Criteria.where("invNumber").is(queryStub.getInvNumber()));
		}

		if (!StringUtils.isEmpty(queryStub.getClient())) {
			List<PurchaseOrder> purchaseOrders = poRepository.findByClientContainingIgnoreCase(queryStub.getClient());
			query.addCriteria(Criteria.where("poNumber")
					.in(purchaseOrders.stream().map(PurchaseOrder::getPoNumber).collect(Collectors.toList())));
		}

		
		if (queryStub.getStartDate() != null && queryStub.getFinishDate() != null) {
			query.addCriteria(Criteria.where("invDate").gte(queryStub.getStartDate()).lte(queryStub.getFinishDate()));
		}else if (queryStub.getStartDate() != null ) {
			query.addCriteria(Criteria.where("invDate").gte(queryStub.getStartDate()));
		}else if (queryStub.getFinishDate() != null ) {
			query.addCriteria(Criteria.where("invDate").lte(queryStub.getFinishDate()));
		}

		if (!StringUtils.isEmpty(queryStub.getStatus())) {
			query.addCriteria(Criteria.where("status").is(queryStub.getStatus()));
		}

		return mongoTemplate.find(query, Invoice.class);
	}

	// public ByteArrayResource downloadInvoice(InvoiceQueryStub queryStub) {
	// log.debug("Invoking downloadInvoice..");
	// ByteArrayResource resource = null;
	// try {
	// StringBuffer sb = new StringBuffer();
	// // Construct and append CSV header
	// String[] header = getInvoiceHeader();
	// sb.append(CsvUtils.getHeader(header, csvDelimiter));
	// // Get data
	// List<Invoice>invoices = findInvoice(queryStub);
	// log.info("payment list size -->" + invoices.size());
	// // Get field list for header
	//
	// // Construct and append CSV content
	// sb.append(CsvUtils.getCsvContent(invoices, header, csvDelimiter));
	// // Convert string to byte array
	// resource = new ByteArrayResource(sb.toString().getBytes("UTF-8"));
	// } catch (IllegalArgumentException | UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// return resource;
	// }

	private String[] getInvoiceHeader() {
		String[] header = { "invNumber", "invDesc", "poNumber", "poDesc", "poCurrency", "term", "termDesc", "invDate",
				"amount", "vat", "paymentPrediction", "status", "incomeTax" };
		return header;
	}

	public ByteArrayResource downloadInvoice(InvoiceQueryStub queryStub) {
		log.debug("Invoking downloadPayment..");
		ByteArrayResource resource = null;
		try {
			StringBuffer sb = new StringBuffer();
			// Construct and append CSV header
			String[] header = getInvoiceHeader();
			sb.append(CsvUtils.getHeader(header, csvDelimiter));
			// Get data
			List<Invoice> invoices = findInvoice(queryStub);
			log.info("invoice list size -->" + invoices.size());
			// Get field list for header

			// Construct and append CSV content
			sb.append(CsvUtils.getCsvContent(invoices, header, csvDelimiter));
			// Convert string to byte array
			resource = new ByteArrayResource(sb.toString().getBytes("UTF-8"));
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resource;
	}

	private ByteArrayResource buildExcelDocument(InvoiceQueryStub queryStub) {
		// Get data
		List<Invoice> invoices = findInvoice(queryStub);

		// create excel xls workbook
		Workbook workbook = new HSSFWorkbook();
		// create excel xls sheet
		Sheet sheet1 = createWorksheet(workbook,"Revenue Report");
		Sheet sheet2 = createWorksheet(workbook,"AR Report");
		
		CellStyle headerStyle = getHeaderStyle(workbook);
		
		generateRevenueReportHeader(sheet1, headerStyle);
		generateRevenueReportContent(invoices, sheet1);
		
		generateARReportHeader(sheet2, headerStyle);
		generateARReportContent(invoices, sheet2);
		
		
		
		
		return null;
	}


	
	private void generateARReportContent(List<Invoice> invoices, Sheet sheet) {
//		Payment Status
//		Invoicing Status
//		Client
//		Name of Project
//		PO Amount
//		Invoice Date
//		INV Number
		int rowCount = 1;
		CellStyle dateStyle =  getDateStyle(sheet.getWorkbook());
		CellStyle amountStyle =  getAmountStyle(sheet.getWorkbook());
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		Collection<InvoiceDistribution> invoiceDist = Arrays.asList(new InvoiceDistribution());
		for (Invoice invoice : invoices) {
			invoiceDist = orElse(invoice.getInvoiceDist(),invoiceDist);
			purchaseOrder = orElse(poRepository.findFirstByPoNumber2(invoice.getPoNumber()) , purchaseOrder);
			
			for (InvoiceDistribution dist : invoiceDist) {
				Row userRow = sheet.createRow(rowCount++);
				userRow.createCell(0).setCellValue(invoice.getStatus());
				userRow.createCell(1).setCellValue(invoice.getInvDate());
				userRow.getCell(1).setCellStyle(dateStyle);
				userRow.createCell(2).setCellValue(dist.getDepartment());
				userRow.createCell(3).setCellValue(dist.getService());
				userRow.createCell(4).setCellValue(purchaseOrder.getClient());
				userRow.createCell(5).setCellValue(purchaseOrder.getPoDesc());
				userRow.createCell(6).setCellValue(purchaseOrder.getPoValue());
				userRow.getCell(6).setCellStyle(amountStyle);
			}

		}

		
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
		
	}

	private Sheet createWorksheet(Workbook workbook,String sheetName) {
		Sheet sheet = workbook.createSheet(sheetName);
		sheet.setDefaultColumnWidth(30);
		return sheet;
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
	
	private CellStyle getDateStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 15); // 0xf, "d-mmm-yy"
		return style;
	}
	
	private CellStyle getAmountStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 4); // "#,##0.00"
		return style;
	}
	
	private void generateRevenueReportHeader(Sheet sheet,CellStyle defaultStyle){
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
	}
	


	
	private void generateRevenueReportContent(List<Invoice> invoices,Sheet sheet) {
		int rowCount = 1;
		CellStyle dateStyle =  getDateStyle(sheet.getWorkbook());
		CellStyle amountStyle =  getAmountStyle(sheet.getWorkbook());
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		Collection<InvoiceDistribution> invoiceDist = Arrays.asList(new InvoiceDistribution());
		Collection<PaymentTerm> paymentTerms = Arrays.asList(new PaymentTerm());
		for (Invoice invoice : invoices) {
			invoiceDist = orElse(invoice.getInvoiceDist(),invoiceDist);
			purchaseOrder = orElse(poRepository.findFirstPaymentTermsByPoNumber(invoice.getPoNumber()) , purchaseOrder);
			paymentTerms =  orElse(purchaseOrder.getPaymentTerms(),paymentTerms);
			
			Optional<PaymentTerm> termOpt = paymentTerms.stream().filter(n -> n.getTerm() == invoice.getTerm())
					.findFirst();		
//			PaymentTerm term = termOpt.orElse(new PaymentTerm());		
					
			for (PaymentTerm term : paymentTerms) {
				Row userRow = sheet.createRow(rowCount++);
				userRow.createCell(0).setCellValue(term.getInvoiceStatus());
				userRow.createCell(1).setCellValue(invoice.getInvDate());
				userRow.getCell(1).setCellStyle(dateStyle);
//				userRow.createCell(2).setCellValue(dist.getDepartment());
//				userRow.createCell(3).setCellValue(dist.getService());
				userRow.createCell(4).setCellValue(purchaseOrder.getClient());
				userRow.createCell(5).setCellValue(purchaseOrder.getPoDesc());
				userRow.createCell(6).setCellValue(purchaseOrder.getPoValue());
				userRow.getCell(6).setCellStyle(amountStyle);
				userRow.createCell(7).setCellValue(invoice.getInvNumber());
			}
			
			

		}
	}
	private <T> Collection<T> getSafeList(Collection<T>dataList,Class<T>clazz) throws Exception{
		return CommonUtils.isEmpty(dataList)
		? Arrays.asList(clazz.newInstance())
		: dataList;
	}
	
	private <T> Collection<T> orElse(Collection<T>dataList1,Collection<T>dataList2){
		return (dataList1 == null) ? dataList2 : dataList1;
	}
	
	
	
	private <T> T orElse(T object1, T object2){
		return (object1 == null) ? object2 : object1;
	}
	
	

}
