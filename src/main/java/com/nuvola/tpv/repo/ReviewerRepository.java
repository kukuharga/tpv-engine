package com.nuvola.tpv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.model.Reviewer;

public interface ReviewerRepository extends MongoRepository<Reviewer, String> {
	
	public List<Reviewer>findByLobType(LobType lobType);
}
