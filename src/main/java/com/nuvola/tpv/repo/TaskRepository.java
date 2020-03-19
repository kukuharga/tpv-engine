package com.nuvola.tpv.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nuvola.tpv.model.Task;

public interface TaskRepository extends MongoRepository<Task, String> {
	public List<Task> findByProjectIdAndTaskType(String projectId, String taskType);

	public List<Task> findByProjectIdAndTaskTypeAndAssignee(String projectId, String taskType, String assignee);

	public List<Task> findByAssignee(String assignee, Sort sort);

	@Transactional
	public Long deleteByProjectId(String projectId);
}
