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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.nuvola.tpv.model.Names;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Review;
import com.nuvola.tpv.model.Task;
import com.nuvola.tpv.model.Names.ReviewStatus;
import com.nuvola.tpv.model.Names.ReviewerType;
import com.nuvola.tpv.model.Names.TaskType;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.TaskRepository;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.service.MailService;
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
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationTests {
	
	private static Log log = LogFactory.getLog(NotificationTests.class);




	
	@Autowired
	private MailService mailService;
	
//	
//
//	@Before
//	public void deleteAllBeforeTests() throws Exception {
//		projectRepository.deleteById("test001");
//		trainingRepository.deleteById("trg001");
//		taskRepository.deleteByProjectId("test001");
//		InputStream inputStream = getClass().getResourceAsStream("/project.json");
//		jsonData_Project = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//		inputStream.close();
//		inputStream = getClass().getResourceAsStream("/training.json");
//		jsonData_Training = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//	}

	@Test
	public void testSendMail() throws Exception {
		Task task = new Task();
		task.setAssignee("kukuh");
		mailService.sendTaskNotification(task);

	}

	
}