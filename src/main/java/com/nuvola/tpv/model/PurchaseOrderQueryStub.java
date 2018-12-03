package com.nuvola.tpv.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

public @Data  class PurchaseOrderQueryStub {
	private String poNumber;
	private String client;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishDate;
	@Override
	public String toString() {
		return "PurchaseOrderQueryStub [poNumber=" + poNumber + ", client=" + client + ", startDate=" + startDate
				+ ", finishDate=" + finishDate + "]";
	}
	
	
}
