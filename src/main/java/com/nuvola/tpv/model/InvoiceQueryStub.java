package com.nuvola.tpv.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

public  @Data class InvoiceQueryStub {
	private String invNumber;
	private String client;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishDate;
	private String status;
	@Override
	public String toString() {
		return "InvoiceQueryStub [invNumber=" + invNumber + ", client=" + client + ", startDate=" + startDate
				+ ", finishDate=" + finishDate + ", status=" + status + "]";
	}
	
	
	
}
