package com.nuvola.tpv.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.Task;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.UserRepository;

@Component
public class MailService {
	private static Log log = LogFactory.getLog(TaskService.class);
	@Autowired
	public JavaMailSender emailSender;
	@Autowired
	public UserRepository userRepository;

	private String emailTemplate;

	private String readEmailTemplate(String filePath) throws IOException {
		return (emailTemplate == null) ? emailTemplate = IOUtils.toString(MailService.class.getResourceAsStream("/email.template"),Charset.forName("UTF-8"))
				: emailTemplate;
	}

	private String getEmailBody(Task task, User user) {
		try {
			

			return readEmailTemplate("/email.template")
					.replaceFirst("%USER%", Optional.ofNullable(user.getFullName()).orElse("-"))
					.replaceFirst("%TASK_TITLE%", Optional.ofNullable(task.getTaskTitle()).orElse("-"))
					.replaceFirst("%PROJECT_NAME%", Optional.ofNullable(task.getProjectName()).orElse("-"))
					.replaceFirst("%ASSIGNED_BY%",
							Optional.ofNullable(userRepository.findByUsername(task.getAssignedBy()))
									.map(User::getFullName).orElse("-"))
					.replaceFirst("%DUE_DATE%", "-")
					.replaceFirst("%TASK_URL%", task.getUrl());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public void sendTaskNotification(Task task) {
		log.debug("Sending mail..1");
		/*
		SimpleMailMessage message = new SimpleMailMessage();
		try {
			User user = userRepository.findByUsername(task.getAssignee());
			if (user == null || StringUtils.isEmpty(user.getEmail()))
				throw new Exception("Email address not found for this user.");
			// message.setTo(user.getEmail());
			message.setTo("kukuh_arga@yahoo.com");
			message.setFrom("noreply@nuvolasystem.com");
			message.setSubject("Action Required: " + task.getTaskTitle() + " for " + task.getProjectName());
			message.setText(getEmailBody(task, user));
			emailSender.send(message);
		} catch (Exception ex) {
			log.debug("Error sending notification email.", ex);
		}
		*/
		
	}

	public void sendTaskNotification(List<Task> taskList) {
		log.debug("Sending mail..2");
		/*
		SimpleMailMessage message = new SimpleMailMessage();
		try {
			taskList.forEach(task -> {
				try {
					User user = userRepository.findByUsername(task.getAssignee());
					if (user == null || StringUtils.isEmpty(user.getEmail()))
						throw new Exception("Email address not found for this user.");
					// message.setTo(user.getEmail());
					message.setTo("kukuh_arga@yahoo.com");
					message.setFrom("noreply@nuvolasystem.com");
					message.setSubject("Action Required: " + task.getTaskTitle() + " for " + task.getProjectName());
					message.setText(getEmailBody(task, user));
					emailSender.send(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});

		} catch (Exception ex) {
			log.debug("Error sending notification email.", ex);
		}
		*/
	}

	public static void main(String[] args) {
	
		
//			InputStream inputStream = MailService.class.getResourceAsStream("/email.template");
//			inputStream.read(b)
		try {
			String data = IOUtils.toString(MailService.class.getResourceAsStream("/email.template"),Charset.forName("UTF-8"));
			System.out.println(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
//			String data = new String(Files.readAllBytes(MailService.class.getResource("/email.template").getPath()));
	
	}

}
