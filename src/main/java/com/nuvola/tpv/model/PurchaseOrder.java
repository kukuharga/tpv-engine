package com.nuvola.tpv.model;

import java.util.Collection;
import java.util.Date;
import org.springframework.data.annotation.Id;
import lombok.Data;

public @Data class PurchaseOrder {
	@Id
	private String id;
	private String poNumber;
	private String poDesc;
	private Date poDate;
	private String poCurrency;
	private double poValue;
	private boolean poHardcopy;
	private boolean poSoftcopy;
	private boolean contractExist;
	private String contractNo;
	private Date contractDate;
	private boolean contractHardcopy;
	private boolean contractSoftcopy;
	private String client;
	private String npwp;
	private Collection<DepartmentRevenue> projectDist;
	private Collection<PaymentTerm> paymentTerms;
	private Collection<InvoiceDistribution> invoiceDist;
	private String remarks;

	
	
}
