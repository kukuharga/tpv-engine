package com.nuvola.tpv.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.DefaultCostItem;


public interface DefaultCostItemRepository extends MongoRepository<DefaultCostItem, String> {
	public List<DefaultCostItem> findByServiceAndCategoryIn(String service,Collection<String>categories);
}
