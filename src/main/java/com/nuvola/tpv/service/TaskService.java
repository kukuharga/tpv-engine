package com.nuvola.tpv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.Names.DecisionType;
import com.nuvola.tpv.model.Names.DocStatus;
import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.model.Names.ReviewStatus;
import com.nuvola.tpv.model.Names.ReviewerType;
import com.nuvola.tpv.model.Names.TaskStatus;
import com.nuvola.tpv.model.Names.TaskType;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Review;
import com.nuvola.tpv.model.Reviewer;
import com.nuvola.tpv.model.Task;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.ReviewerRepository;
import com.nuvola.tpv.repo.TaskRepository;
import com.nuvola.tpv.repo.UserRepository;

@Component
public class TaskService {
	private static Log log = LogFactory.getLog(TaskService.class);
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private ReviewerRepository reviewerRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MailService mailService;
	
//	@Autowired
//	private TrainingService trainingService;
//	@Autowired
//	private InstallationService installService;
//	@Autowired
//	private MandaysServiceService mandaysService;
//	@Autowired
//	private PurchaseService purchaseService;
	

	public Task createTPVTask(Project project, String assignee) {
		Task task = new Task();
		task.setProjectId(project.getId());
		task.setProjectName(project.getName());
		task.setLobType(project.getService());
		task.setTaskType(TaskType.CREATE_TPV);
		task.setTaskDesc("Create project estimation.");
		task.setTaskTitle("Create TPV");
		task.setAssignee(assignee);
		task.setAssignedBy(project.getSales());
		task.setUrl(getCreateTPVURL(project.getService(), project.getId()));
		return taskRepository.save(task);
	}

	private String getCreateTPVURL(LobType lobType, String projectID) {
		String url = null;
		switch (lobType) {
		case MANDAYS:
			url = "lob-service.html?projectId=" + projectID + "&action=edit";
			break;
		case TRAINING:
			url = "lob-training.html?projectId=" + projectID + "&action=create";
			break;
		case INSTALL:
			url = "lob-installation.html?projectId=" + projectID + "&action=create";
			break;
		case PURCHASE:
			url = "lob-license.html?projectId=" + projectID + "&action=edit";
			break;
		default:
			break;
		}
		return url;
	}

	private String getReviewTPVURL(LobType lobType, String projectID) {
		String url = null;
		switch (lobType) {
		case MANDAYS:
			url = "lob-service-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case TRAINING:
			url = "lob-training-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case INSTALL:
			url = "lob-install-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case PURCHASE:
			url = "lob-license-vw.html?projectId=" + projectID + "&action=approval";
			break;
		default:
			break;
		}
		return url;
	}

	public Task createTPVReviewerTask(Project project, String assignee, String assignedBy) {
		Task task = new Task();
		task.setProjectId(project.getId());
		task.setProjectName(project.getName());
		task.setLobType(project.getService());
		task.setTaskType(TaskType.REVIEW_TPV);
		task.setTaskDesc("Review service estimation.");
		task.setTaskTitle("Review TPV");
		task.setUrl(getReviewTPVURL(project.getService(), project.getId()));
		task.setAssignee(assignee);
		task.setAssignedBy(assignedBy);
		task.setUrl(getTPVApprovalUrl(project.getService(), project.getId()));
		return taskRepository.save(task);
	}

	private String getTPVApprovalUrl(LobType lobType, String projectID) {
		String url = null;
		switch (lobType) {
		case MANDAYS:
			url = "lob-service-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case TRAINING:
			url = "lob-training-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case INSTALL:
			url = "lob-install-vw.html?projectId=" + projectID + "&action=approval";
			break;
		case PURCHASE:
			url = "lob-license-vw.html?projectId=" + projectID + "&action=approval";
			break;
		default:
			break;
		}
		return url;
	}

	public Task createRLTReviewerTask(Project project, String assignee, String assignedBy) {
		Task task = new Task();
		task.setProjectId(project.getId());
		task.setProjectName(project.getName());
		task.setLobType(project.getService());
		task.setTaskType(TaskType.REVIEW_RLT);
		task.setTaskDesc("Review project estimation.");
		task.setAssignee(assignee);
		task.setAssignedBy(assignedBy);
		task.setTaskTitle("Review RLT");
		task.setUrl("project-vw.html?projectId=" + project.getId() + "&action=approval");
		return taskRepository.save(task);
	}

	public void assignTPVTask(Project project) {
		mailService.sendTaskNotification(createTPVTask(project, project.getPresales1()));
	}

	public void createTPVReviewerTask(Project project, String userName) {
		if (project.getReviews() == null)
			return;
		
		Set<Review> reviews = project.getReviews().stream().filter(r -> r.getReviewerType() == ReviewerType.LOB).collect(Collectors.toSet());
		if(reviews == null) reviews = new HashSet<>();
		reviews.add(new Review(project.getPmoDelivery(),ReviewerType.LOB,DecisionType.INDIVIDUAL));
		reviews.forEach(r2 -> {
			mailService.sendTaskNotification(createTPVReviewerTask(project, r2.getReviewer(), userName));
		});
		
		reviews.stream().forEach(review -> review.setStatus(ReviewStatus.PENDING_APPROVAL));
		//add back new reviewer to the project
		project.addAllReviews(reviews);
	}

	public void createRLTReviewerTask(Project project, String userName) {
		if (project.getReviews() == null)
			return;
		List<Task> taskList = project.getReviews().stream().filter(r -> r.getReviewerType() == ReviewerType.PROJECT)
		.map(r2 -> createRLTReviewerTask(project, r2.getReviewer(), userName)).collect(Collectors.toList());
		mailService.sendTaskNotification(taskList);
	}

	public ReviewStatus getReviewStatus(Set<Review> reviews) {
		if (reviews == null)
			return null;
		Map<String, ReviewStatus> decisionMap = new HashMap<>();
		reviews.stream().filter(r -> r.getReviewerType() == ReviewerType.PROJECT).collect(Collectors.groupingBy(Review::getDecisionKey,
				Collectors.mapping(Review::getStatus, Collectors.toList())))
		.forEach((k, v) -> {decisionMap.put(k, getDecision(v));});

		return getDecision(decisionMap);
	}

	public ReviewStatus getTPVReviewStatus(Set<Review> reviews) {
		if (reviews == null)
			return null;
		Map<String, ReviewStatus> decisionMap = new HashMap<>();
		reviews.stream().filter(r -> r.getReviewerType() == ReviewerType.LOB)
				.collect(Collectors.groupingBy(Review::getDecisionKey,
						Collectors.mapping(Review::getStatus, Collectors.toList())))
				.forEach((k, v) -> {
					decisionMap.put(k, getDecision(v));
				});

		return getDecision(decisionMap);
	}

	public boolean isRLTApproved(Set<Review> reviews) {
		return getReviewStatus(reviews) == ReviewStatus.APPROVED;
	}

//	public void onTPVApproved(Project project) {
//		createRLTReviewerTask(project, project.getLastModifiedBy());
//		project.setDocStatus(DocStatus.TPV_APPROVED);
//
//		switch (project.getService()) {
//		case MANDAYS:
//			mandaysService.setRevenueAndCost(project);
//			break;
//		case TRAINING:
//			trainingService.setRevenueAndCost(project);
//			break;
//		case INSTALL:
//			installService.setRevenueAndCost(project);
//			break;
//		case PURCHASE:
//			purchaseService.setRevenueAndCost(project);
//			break;
//		}
//	}

	private ReviewStatus getDecision(Map<String, ReviewStatus> decisionMap) {
		int approved = 0, declined = 0;
		
		for (String key : decisionMap.keySet()) {
			ReviewStatus reviewStatus = decisionMap.get(key);
			if(reviewStatus == null) continue;
			switch (decisionMap.get(key)) {
			case DECLINED:
				declined++;
				break;
			case APPROVED:
				approved++;
				break;
			}

		}


		if (declined > 0)
			return ReviewStatus.DECLINED;
		if (approved == decisionMap.size())
			return ReviewStatus.APPROVED;
		return null;
	}

	private ReviewStatus getDecision(List<ReviewStatus> reviews) {
		int declined = 0, approved = 0;
		for (ReviewStatus reviewStatus : reviews) {
			if(reviewStatus == null) continue;
			switch (reviewStatus) {
			case APPROVED:
				approved++;
				break;
			case DECLINED:
				declined++;
				break;
			}
		}
		if (declined > 0)
			return ReviewStatus.DECLINED;
		if (approved > 0)
			return ReviewStatus.APPROVED;
		return null;

	}

	public void onRLTDraftSubmitted(Project project) {
		log.info("on RLT Draft Submitted..");
		addReviewers(project);
		assignTPVTask(project);
	}

	private Set<Review> getReviews(List<Reviewer> reviewers) {
		Set<Review> reviews = new HashSet<>();
		reviewers.forEach(n -> {
			switch (n.getUserType()) {
			case USER:

				reviews.add(new Review(n.getReviewer(), n.getReviewerType(), DecisionType.INDIVIDUAL));
				break;
			case GROUP:
				List<User> users = userRepository.findByGroups(n.getReviewer());
				users.forEach(user -> {
					reviews.add(new Review(user.getUsername(), n.getReviewerType(), DecisionType.REPRESENTATIVE,
							n.getReviewer()));
				});
				break;
			}

		});
		return reviews;
	}

	public void addReviewers(Project project) {
		List<Reviewer> reviewers = reviewerRepository.findByLobType(project.getService());
		if (reviewers != null) log.info(reviewers.size() + " reviewers found.");
		project.addAllReviews(getReviews(reviewers));
		projectRepository.save(project);
	}

	public void submitTpvLobService(MandaysService mandaysService) {
		List<Task> taskList = taskRepository.findByProjectIdAndTaskType(mandaysService.getProjectId(),
				TaskType.CREATE_TPV.toString());
		taskList.forEach(t -> {
			t.setCompletedDate(new Date());
			t.setCompletedBy(mandaysService.getLastModifiedBy());
			t.setTaskStatus(TaskStatus.COMPLETED);
		});
		taskRepository.saveAll(taskList);

		Project project = projectRepository.findById(mandaysService.getProjectId()).get();
		project.setDocStatus(DocStatus.WAITING_TPV_APPROVAL);
		projectRepository.save(project);
		createTPVReviewerTask(project, mandaysService.getLastModifiedBy());

	}

	public List<Task> findByAssignee(String assignee) {
		return taskRepository.findByAssignee(assignee, Sort.by(Sort.Direction.DESC, "createdDate"));
	}

	public List<Task> findByProjectIdAndTaskType(String projectId, String taskType) {
		return taskRepository.findByProjectIdAndTaskType(projectId, taskType);
	}

	public void onTPVReviewCompleted(String projectId, String userName) {
		List<Task> taskList = taskRepository.findByProjectIdAndTaskTypeAndAssignee(projectId,
				TaskType.REVIEW_TPV.toString(), userName);
		taskList.forEach(t -> {
			t.setCompletedDate(new Date());
			t.setCompletedBy(userName);
			t.setTaskStatus(TaskStatus.COMPLETED);
		});
		taskRepository.saveAll(taskList);
	}

	public void onRLTReviewCompleted(String projectId, String userName) {
		List<Task> taskList = taskRepository.findByProjectIdAndTaskTypeAndAssignee(projectId,
				TaskType.REVIEW_RLT.toString(), userName);
		taskList.forEach(t -> {
			t.setCompletedDate(new Date());
			t.setCompletedBy(userName);
			t.setTaskStatus(TaskStatus.COMPLETED);
		});
		taskRepository.saveAll(taskList);
	}

//	public void onRLTApproved(Project project) {
//		log.info("on RLT Approved..");
//		project.setDocStatus(DocStatus.APPROVED);
//
//	}

	public boolean isTPVApproved(Set<Review> reviews) {
		return getTPVReviewStatus(reviews) == ReviewStatus.APPROVED;
	}

	public boolean isTPVDeclined(Set<Review> reviews) {
		return getTPVReviewStatus(reviews) == ReviewStatus.DECLINED;
	}

	public void onTPVDeclined(Project project) {
		project.setDocStatus(DocStatus.TPV_DECLINED);

	}

	public Task findById(String taskId) {
		return taskRepository.findById(taskId).get();

	}

	public void onTPVSubmitted(Project project) {
		List<Task> taskList = taskRepository.findByProjectIdAndTaskType(project.getId(),TaskType.CREATE_TPV.toString());
		taskList.forEach(t -> {
			t.setCompletedDate(new Date());
			t.setCompletedBy(project.getLastModifiedBy());
			t.setTaskStatus(TaskStatus.COMPLETED);
		});
		taskRepository.saveAll(taskList);
		
		

		
		project.setDocStatus(DocStatus.WAITING_TPV_APPROVAL);
		createTPVReviewerTask(project, project.getPresales1());
		projectRepository.save(project);
	}
	

	
	

}
