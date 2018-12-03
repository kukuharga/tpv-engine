package com.nuvola.tpv.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.MandaysSummary;

public interface MandaysSummaryRepository extends MongoRepository<MandaysSummary, String> {
	
	
	
}
