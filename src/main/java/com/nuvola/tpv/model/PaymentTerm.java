package com.nuvola.tpv.model;

import java.util.Date;
import lombok.Data;

public @Data class PaymentTerm {
	private int term; //Term #
	private String termDesc; //Term Description
	private double amount; //Payment Amount
	private Date invPredictionDt; //Estimated Invoice Date
	private String invoiceStatus = "UNBILLED";
	
	
}
