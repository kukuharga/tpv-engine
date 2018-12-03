package com.nuvola.tpv.model;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import lombok.Data;

public @Data class Invoice {
	@Id
	private String id;
	private String invNumber; //Invoice #
	private String invDesc; //Invoice Description
	private String poNumber; //PO #
	private String poDesc; //PO Description
	private String poId; //PO ID
	private String poCurrency; //PO Currency
	private int term; //Term #
	private String termDesc; //Term #
	private Date invDate; //Invoice Date
	private double amount; //Invoice Amount
	private double vat; //VAT (0 - 1)
	private Date paymentPrediction; //Est.Payment Date
	private Collection<InvoiceDistribution> invoiceDist;
	private String status ="UNPAID";
	private double incomeTax;
	
	public double getTotalInvoiceDist() {
		if (invoiceDist == null) return 0;
		return invoiceDist.stream().collect(Collectors.summingDouble(InvoiceDistribution::getAmount));
	}
	
	//Total Invoice
	public double getTotalInvoice() {
		return this.amount + this.vat;
	}
	
	public String getInvoiceString() {
		return this.term + " - " + this.invDesc;
	}
	
	
	
	
}
