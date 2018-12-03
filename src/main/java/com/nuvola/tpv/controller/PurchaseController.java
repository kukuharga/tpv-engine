package com.nuvola.tpv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.repo.PurchaseRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nydiarra on 06/05/17.
 */
@RestController
@RequestMapping("/purchases")
public class PurchaseController {
	@Autowired
	private PurchaseRepository purchaseRepository;

	
	@RequestMapping(value = "/byProject/{projectId}", method = RequestMethod.GET)
	public List<Purchase> getPurchase(@PathVariable String projectId){
		return purchaseRepository.findByProjectId(projectId);
	}
	
	@RequestMapping(value = "/bulkSave")
	public ResponseEntity<List<Purchase>> savePurchase(@RequestBody List <Purchase> purchaseList){
//	System.out.println("project id:"+purchaseList.size());
	System.out.println("purchase List size:"+purchaseList.size());
	System.out.println(Arrays.toString(purchaseList.toArray()));
		return new ResponseEntity<List<Purchase>>(purchaseList,HttpStatus.OK);
	}
	

	
	
}
