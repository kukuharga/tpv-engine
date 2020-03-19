package com.nuvola.tpv.model;



import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;


import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class Comment extends Auditable<String>{
	@Id
	private String id;
	private String parent;
	private String type;
	private String refId;
	private String content;
	private List<String>pings;
	private String fullname;
	private boolean created_by_admin;
	private boolean created_by_current_user;
	private int upvote_count;
	private boolean user_has_upvoted;
	private boolean is_new;
	private String profile_picture_url;
	
	public Date getCreated() {
		return getCreatedDate();
	}
	
	public Date getModified() {
		return super.getLastModifiedDate();
	}
	
	public String getCreator() {
		return super.getCreatedBy();
	}
	
	public void setCreated(Date date) {
		super.setCreatedDate(date);
	}
	
	public void setModified(Date date) {
		super.setLastModifiedDate(date);
	}
	
	public void setCreator(String username) {
		super.setCreatedBy(username);
	}
	
	
	
}
