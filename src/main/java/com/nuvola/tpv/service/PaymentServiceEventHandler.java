package com.nuvola.tpv.service;


import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.repo.InvoiceRepository;

@Component
@RepositoryEventHandler(Payment.class)
public class PaymentServiceEventHandler {
	private static Logger log = Logger.getLogger(PaymentServiceEventHandler.class.getName());

	@Autowired
	private InvoiceRepository invoiceRepository;
	

	@HandleAfterCreate
	@HandleAfterSave
	public void handlePaymentSave(Payment payment) {
		log.info("==after save payment==" + payment.getInvNumber());
		Invoice invoice =  invoiceRepository.findFirstByInvNumber(payment.getInvNumber());
		if(invoice == null) return;
		invoice.setStatus("PAID");
		invoiceRepository.save(invoice);
	}

	

}