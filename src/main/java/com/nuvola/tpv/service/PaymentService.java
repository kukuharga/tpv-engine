package com.nuvola.tpv.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.PaymentQueryStub;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.repo.PurchaseOrderRepository;

@Component
public class PaymentService {
	private static Log log = LogFactory.getLog(PaymentService.class);
	@Autowired
	private PurchaseOrderRepository poRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Value("${csv.delimiter}")
	private String csvDelimiter;

	public List<Payment> getPayment(PaymentQueryStub queryStub) {

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
			query.addCriteria(Criteria.where("paymentDt").gte(queryStub.getStartDate()).lte(queryStub.getFinishDate()));
		}else if (queryStub.getStartDate() != null ) {
			query.addCriteria(Criteria.where("paymentDt").gte(queryStub.getStartDate()));
		}else if (queryStub.getFinishDate() != null ) {
			query.addCriteria(Criteria.where("paymentDt").lte(queryStub.getFinishDate()));
		}
		
		
		
		if (!StringUtils.isEmpty(queryStub.getStatus())) {
			query.addCriteria(Criteria.where("status").is(queryStub.getStatus()));
		}


		return mongoTemplate.find(query, Payment.class);
	}

	public ByteArrayResource downloadPayment(PaymentQueryStub queryStub) {
		log.debug("Invoking downloadPayment..");
		ByteArrayResource resource = null;
		try {
			StringBuffer sb = new StringBuffer();
			// Construct and append CSV header
			String[] header = getPaymentHeader();
			sb.append(CsvUtils.getHeader(header, csvDelimiter));  
			// Get data
			List<Payment> payments = getPayment(queryStub);
			log.info("payment list size -->" + payments.size());
			// Get field list for header

			// Construct and append CSV content
			sb.append(CsvUtils.getCsvContent(payments, header, csvDelimiter));
			// Convert string to byte array
			resource = new ByteArrayResource(sb.toString().getBytes("UTF-8"));
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resource;
	}

	private String[] getPaymentHeader() {
		String[] header = { "poNumber", "poDesc", "invNumber", "term", "termDesc", "paymentDt", "paymentAmt", "remarks",
				"incomeTax", "vat", "others", "otherRemarks", "outstandingAmt", "outstandingNotes", "totalInvoiceAmt",
				"currency", "status" };
		return header;
	}

}
