package com.nuvola.tpv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nuvola.tpv.model.Comment;
import com.nuvola.tpv.model.Names.CommentType;
import com.nuvola.tpv.model.Names.LobType;


public interface CommentRepository extends MongoRepository<Comment, String> {
	
	public List<Comment>findByTypeAndRefId(CommentType type,String refId);
}
