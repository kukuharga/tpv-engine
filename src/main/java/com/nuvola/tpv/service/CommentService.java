package com.nuvola.tpv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.Comment;
import com.nuvola.tpv.model.Names.CommentType;
import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.repo.CommentRepository;

@Component
public class CommentService {
	private static final String PROJECT_TYPE = "PROJECT";
	private static final String LOB_MANDAYS = "MANDAYS";
	private static final String LOB_TRAINING = "TRAINING";
	private static final String LOB_INSTALLATION = "INSTALLATION";
	private static final String LOB_PURCHASING = "PURCHASING";
	@Autowired 
	private CommentRepository commentRepository;
	
	public List<Comment>getProjectComment(String projectId) {
		return commentRepository.findByTypeAndRefId(CommentType.PROJECT, projectId);
	}
	
	public List<Comment>getLobComment(String projectId) {
		return commentRepository.findByTypeAndRefId(CommentType.LOB, projectId);
	}
	
}
