package com.nuvola.tpv.repo;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.Menu;

public interface MenuRepository extends MongoRepository<Menu, String> {
	List<Menu> findBySubMenuCode(String code);
	
}
