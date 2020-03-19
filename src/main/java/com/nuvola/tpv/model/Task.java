package com.nuvola.tpv.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.model.Names.TaskStatus;
import com.nuvola.tpv.model.Names.TaskType;
import lombok.Data;

public @Data class Task extends Auditable<String> {
	@Id
	private String code;
	private String assignee;
	private LobType lobType;
	private String taskDesc;
	private TaskType taskType;
	private Date completedDate;
	private String projectId;
	private String projectName;
	private String completedBy;
	private String assignedBy;
	private TaskStatus taskStatus = TaskStatus.OPEN;
	private String url;
	private String taskTitle;
	
}
