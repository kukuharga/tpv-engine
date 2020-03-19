/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nuvola.tpv;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.nuvola.tpv.model.Names;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Review;
import com.nuvola.tpv.model.Task;
import com.nuvola.tpv.model.Names.TaskType;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.TaskRepository;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.service.ProjectService;



import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApprovalTests {
	
	private static Log log = LogFactory.getLog(ApprovalTests.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private TrainingRepository trainingRepository;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private TaskRepository taskRepository;

	private String jsonData_Project;
	
	private String jsonData_Training;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		projectRepository.deleteById("test001");
		trainingRepository.deleteById("trg001");
		taskRepository.deleteByProjectId("test001");
		InputStream inputStream = getClass().getResourceAsStream("/project.json");
		jsonData_Project = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();
		inputStream = getClass().getResourceAsStream("/training.json");
		jsonData_Training = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$._links.projects").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("projects/")));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated())
				.andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("TB1-TABLEAU ADVANCE"))
				.andExpect(jsonPath("$.poNumber").value("PO_123"));
	}

	@Test
	public void shouldQueryEntity() throws Exception {

		mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated());

		mockMvc.perform(get("/projects/{projectId}", "test001")).andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("TB1-TABLEAU ADVANCE"));
	}
	
	@Test
	public void tpvFullyApprovedByIndividual() throws Exception {
		
		//Submit New Project
		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated()).andReturn();
		String location = mvcResult.getResponse().getHeader("Location");
		Project project = projectService.getProject("test001");
		assertNotNull(project);//Check if project is created
		assertEquals(Names.DocStatus.WAITING_TPV_SUBMISSION, project.getDocStatus());
		
		
		List<Task>taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.CREATE_TPV.toString());
		assertEquals(1, taskList.size());
		
		mockMvc.perform(post("/trainings").content(jsonData_Training)).andExpect(status().isCreated());
		mockMvc.perform(patch(location).content("{\"docStatus\":\"WAITING_TPV_APPROVAL\"}")).andExpect(status().isNoContent());
		project = projectService.getProject("test001");
		log.info("Submitting LOB..");
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_TPV.toString());
		assertEquals(3, taskList.size());
		
		//TPV APPROVAL
		Map<String,String> params = new HashMap<>();
		params.put("reviewStatus", "APPROVED");
		params.put("reviewType", "LOB");
		projectService.setApprovalStatus(project.getId(), "cayaluna", params);
		projectService.setApprovalStatus(project.getId(), "raditya", params);
		projectService.setApprovalStatus(project.getId(), "tita", params);
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.TPV_APPROVED, project.getDocStatus());
		assertTrue(project.getRevenue() > 0);
		assertTrue(project.getCost() > 0);
		
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_RLT.toString());
		assertEquals(3, taskList.size());
		
		
//		assertTrue(project.getRevenue() > 0);
//		assertTrue(project.getCost() > 0);
		

		
	}
	
	@Test
	public void rltFullyApprovedByIndividual() throws Exception {
		
		//Submit New Project
		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated()).andReturn();
		String location = mvcResult.getResponse().getHeader("Location");
		Project project = projectService.getProject("test001");
		assertNotNull(project);//Check if project is created
		assertEquals(Names.DocStatus.WAITING_TPV_SUBMISSION, project.getDocStatus());
		
		
		List<Task>taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.CREATE_TPV.toString());
		assertEquals(1, taskList.size());
		
		mockMvc.perform(post("/trainings").content(jsonData_Training)).andExpect(status().isCreated());
		mockMvc.perform(patch(location).content("{\"docStatus\":\"WAITING_TPV_APPROVAL\"}")).andExpect(status().isNoContent());
		project = projectService.getProject("test001");
		log.info("Submitting LOB..");
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_TPV.toString());
		assertEquals(4, taskList.size());
		
		//TPV APPROVAL
		Map<String,String> params = new HashMap<>();
		params.put("reviewStatus", "APPROVED");
		params.put("reviewType", "LOB");
		projectService.setApprovalStatus(project.getId(), "cayaluna", params);
		projectService.setApprovalStatus(project.getId(), "raditya", params);
		projectService.setApprovalStatus(project.getId(), "tita", params);
		projectService.setApprovalStatus(project.getId(), "user-3", params);
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.TPV_APPROVED, project.getDocStatus());
		assertTrue(project.getRevenue() > 0);
		assertTrue(project.getCost() > 0);
		
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_RLT.toString());
		assertEquals(3, taskList.size());
		String ovrRevenue = "50000000000";
		// RLT APPROVAL
		Map<String,String> approveParams = new HashMap<>();
		approveParams.put("reviewStatus", "APPROVED");
		approveParams.put("reviewType", "PROJECT");
		approveParams.put("ovrRevenue", ovrRevenue);
		
		Map<String,String> rejectParams = new HashMap<>();
		rejectParams.put("reviewStatus", "DECLINED");
		rejectParams.put("reviewType", "PROJECT");
		
		projectService.setApprovalStatus(project.getId(), "nia", approveParams);
		projectService.setApprovalStatus(project.getId(), "arman", approveParams);
		projectService.setApprovalStatus(project.getId(), "agus_ompong", rejectParams);
		project = projectService.getProject("test001");
		double ovrRev = Double.valueOf(ovrRevenue);
		assertTrue(ovrRev == project.getOvrRevenue());
		assertEquals(Names.DocStatus.APPROVED, project.getDocStatus());
		
		
		

		
	}
	
	@Test
	public void tpvFullyApprovedByGroup() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated()).andReturn();
		String location = mvcResult.getResponse().getHeader("Location");
		
		Project project = projectService.getProject("test001");
		project.getReviews().add(new Review("rio", Names.ReviewerType.LOB, Names.DecisionType.REPRESENTATIVE, "DLV"));
		project.getReviews().add(new Review("luthfy", Names.ReviewerType.LOB, Names.DecisionType.REPRESENTATIVE, "DLV"));
		project = projectRepository.save(project);
		
		assertNotNull(project);
		assertEquals(Names.DocStatus.WAITING_TPV_SUBMISSION, project.getDocStatus());
		List<Task>taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.CREATE_TPV.toString());
		assertEquals(1, taskList.size());
		
		mockMvc.perform(post("/trainings").content(jsonData_Training)).andExpect(status().isCreated());
		
		log.info("Submitting LOB..");
		mockMvc.perform(patch(location).content("{\"docStatus\":\"WAITING_TPV_APPROVAL\"}")).andExpect(status().isNoContent());
		project = projectService.getProject("test001");	
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		assertTrue(project.getRevenue() == 0); //Revenue & Cost Section should not have been set at this stage
		assertTrue(project.getCost() == 0);
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_TPV.toString());
		assertEquals(5, taskList.size());
		
		
		Map<String,String> params = new HashMap<>();
		params.put("reviewStatus", "APPROVED");
		params.put("reviewType", "LOB");
		projectService.setApprovalStatus(project.getId(), "cayaluna",params);
		projectService.setApprovalStatus(project.getId(), "raditya",params);
		projectService.setApprovalStatus(project.getId(), "tita",params);
		projectService.setApprovalStatus(project.getId(), "luthfy",params);
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.TPV_APPROVED, project.getDocStatus());
		assertTrue(project.getRevenue() > 0);
		assertTrue(project.getCost() > 0);
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_RLT.toString());
		assertEquals(3, taskList.size());
		

		
	}
	

	
	@Test
	public void tpvPartiallyApproved() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated()).andReturn();
		String location = mvcResult.getResponse().getHeader("Location");
		
		Project project = projectService.getProject("test001");
		assertNotNull(project);
		assertEquals(Names.DocStatus.WAITING_TPV_SUBMISSION, project.getDocStatus());
		List<Task>taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.CREATE_TPV.toString());
		assertEquals(1, taskList.size());
		mockMvc.perform(post("/trainings").content(jsonData_Training)).andExpect(status().isCreated());
		mockMvc.perform(patch(location).content("{\"docStatus\":\"WAITING_TPV_APPROVAL\"}")).andExpect(status().isNoContent());
		project = projectService.getProject("test001");
		log.info("Submitting LOB..");
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_TPV.toString());
		assertEquals(3, taskList.size());

		Map<String,String> params = new HashMap<>();
		params.put("reviewStatus", "APPROVED");
		params.put("reviewType", "LOB");
		projectService.setApprovalStatus(project.getId(), "cayaluna",params);
		projectService.setApprovalStatus(project.getId(), "raditya",params);
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		assertTrue(project.getRevenue() == 0);
		assertTrue(project.getCost() == 0);
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_RLT.toString());
		assertEquals(0, taskList.size());

		

		
	}
	
	@Test
	public void tpvPartiallyDeclined() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/projects").content(jsonData_Project)).andExpect(status().isCreated()).andReturn();
		String location = mvcResult.getResponse().getHeader("Location");
		
		Project project = projectService.getProject("test001");
		assertNotNull(project);
		assertEquals(Names.DocStatus.WAITING_TPV_SUBMISSION, project.getDocStatus());
		
		List<Task>taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.CREATE_TPV.toString());
		assertEquals(1, taskList.size());
		
		mockMvc.perform(post("/trainings").content(jsonData_Training)).andExpect(status().isCreated());
		mockMvc.perform(patch(location).content("{\"docStatus\":\"WAITING_TPV_APPROVAL\"}")).andExpect(status().isNoContent());
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.WAITING_TPV_APPROVAL, project.getDocStatus());
		
		log.info("Submitting LOB..");
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_TPV.toString());
		assertEquals(3, taskList.size());
		
		Map<String,String> params = new HashMap<>();
		params.put("reviewStatus", "APPROVED");
		params.put("reviewType", "LOB");
		projectService.setApprovalStatus(project.getId(), "cayaluna",params);
		projectService.setApprovalStatus(project.getId(), "raditya",params);
		projectService.setApprovalStatus(project.getId(), "tita",params);
		project = projectService.getProject("test001");
		assertEquals(Names.DocStatus.TPV_DECLINED, project.getDocStatus());
		assertTrue(project.getRevenue() == 0);
		assertTrue(project.getCost() == 0);
		
		
		taskList = taskRepository.findByProjectIdAndTaskType("test001", TaskType.REVIEW_RLT.toString());
		assertEquals(0, taskList.size());

		

		
	}
	
	//
	// @Test
	// public void shouldUpdateEntity() throws Exception {
	//
	// MvcResult mvcResult = mockMvc.perform(post("/people").content(
	// "{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
	// status().isCreated()).andReturn();
	//
	// String location = mvcResult.getResponse().getHeader("Location");
	//
	// mockMvc.perform(put(location).content(
	// "{\"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
	// status().isNoContent());
	//
	// mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
	// jsonPath("$.firstName").value("Bilbo")).andExpect(
	// jsonPath("$.lastName").value("Baggins"));
	// }
	//
	// @Test
	// public void shouldPartiallyUpdateEntity() throws Exception {
	//
	// MvcResult mvcResult = mockMvc.perform(post("/people").content(
	// "{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
	// status().isCreated()).andReturn();
	//
	// String location = mvcResult.getResponse().getHeader("Location");
	//
	// mockMvc.perform(
	// patch(location).content("{\"firstName\": \"Bilbo Jr.\"}")).andExpect(
	// status().isNoContent());
	//
	// mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
	// jsonPath("$.firstName").value("Bilbo Jr.")).andExpect(
	// jsonPath("$.lastName").value("Baggins"));
	// }
	//
	// @Test
	// public void shouldDeleteEntity() throws Exception {
	//
	// MvcResult mvcResult = mockMvc.perform(post("/people").content(
	// "{ \"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
	// status().isCreated()).andReturn();
	//
	// String location = mvcResult.getResponse().getHeader("Location");
	// mockMvc.perform(delete(location)).andExpect(status().isNoContent());
	//
	// mockMvc.perform(get(location)).andExpect(status().isNotFound());
	// }
}