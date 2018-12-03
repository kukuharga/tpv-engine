package com.nuvola.tpv.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

public @Data class PaymentQueryStub {
	private String client;
	private String invNumber;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishDate;
	private String status;
	@Override
	public String toString() {
		return "PaymentQueryStub [client=" + client + ", invNumber=" + invNumber + ", startDate=" + startDate
				+ ", finishDate=" + finishDate + ", status=" + status + "]";
	}
	
	
	
	
}
