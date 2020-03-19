package com.nuvola.tpv.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailServerConfig {
	

	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.port}")
	private String port;

	@Value("${spring.mail.username}")
	private String userName;

	@Value("${spring.mail.password}")
	private String password;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String startTlsEnable;
	
	@Bean
	public JavaMailSender getEmailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost(this.host);
	    mailSender.setPort(Integer.valueOf(this.port));
	     
	    mailSender.setUsername(this.userName);
	    mailSender.setPassword(this.password);
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", Boolean.valueOf(this.smtpAuth));
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    props.put("mail.smtp.ssl.enable","true");
	     
	    return mailSender;
	}

}
