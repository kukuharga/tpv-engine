package com.nuvola.tpv.service;


import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.PaymentTerm;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.repo.InvoiceRepository;
import com.nuvola.tpv.repo.PurchaseOrderRepository;

@Component
@RepositoryEventHandler(Invoice.class)
public class InvoiceServiceEventHandler {
	private static Logger log = Logger.getLogger(InvoiceServiceEventHandler.class.getName());

	@Autowired
	private PurchaseOrderRepository poRepository;
	

	@HandleAfterCreate
	@HandleAfterSave
	public void handleInvoiceSave(Invoice invoice) {
		log.info("==after save payment==" + invoice.getPoNumber());
		PurchaseOrder purchaseOrder =  poRepository.findFirstByPoNumber(invoice.getPoNumber());
		if(purchaseOrder == null) return;
		Collection<PaymentTerm> termList = purchaseOrder.getPaymentTerms();
		if(termList == null) return;
		termList.stream().filter(n -> n.getTerm() == invoice.getTerm()).forEach(x -> x.setInvoiceStatus("BILLED"));
		poRepository.save(purchaseOrder);
	}

	

}