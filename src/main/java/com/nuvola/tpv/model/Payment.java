package com.nuvola.tpv.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Payment {
	@Id
	private String id;
	private String poNumber;
	private String poDesc;
	private String invNumber;
	private int term;
	private String termDesc;
	private Date paymentDt;
	private double paymentAmt;
	private String remarks;
	private double incomeTax; //PPH
	private double vat; //PPN
	private double others; 
	private String otherRemarks; //TRANSFER_FEE
	private double outstandingAmt;
	private String outstandingNotes;
	private double totalInvoiceAmt;
	private String currency;
	private String status;
}
