package com.nuvola.tpv.config;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nuvola.tpv.controller.ProjectController;

/**
 * Created by kukuharga on 06/05/18.
 */
@Aspect
@Configuration
@Order(-2)	
public class AdditionalWebConfig {
	private static Log log = LogFactory.getLog(ProjectController.class);
	/**
	 * Allowing all origins, headers and methods here is only intended to keep this
	 * example simple. This is not a default recommended configuration. Make
	 * adjustments as necessary to your use case.
	 *
	 */
	
	@Value("${allowed-origins}")
	private String allowedOrigins;
//	
//	@Value("${spring.data.mongodb.database}")
//	private String mongodbDatabase;
//	
//	@Bean
//	public  MongoClient mongoClient() {
//		return new MongoClient(mongoHost);
//	}
//
//	@Bean
//	public  MongoTemplate mongoTemplate() {
//		return new MongoTemplate(mongoClient(), mongodbDatabase);
//	}


	@Bean
	public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		log.debug("====FilterRegistrationBean"+ Arrays.asList(allowedOrigins.split(",")));
		config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
	


	 
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				log.debug("====addCorsMappings "+Arrays.toString(allowedOrigins.split(",")) );
				registry.addMapping("/**").allowedOrigins(allowedOrigins.split(",")).allowedHeaders("*").allowedMethods("*");
			}
		};
	}
	
//	@Pointcut("within(@org.springframework.stereotype.Repository *)")
//	public void repositoryClassMethods() {
//		System.out.println("===wiiiwww===");
//	}
	
	@Around("execution(* com.nuvola.tpv.model.Project.getId())")
	public void projectAroundAdvice() {
		log.debug("===wooowww MDEBUG===");
		System.out.println("===wooowww===");
//		return pjp.getTarget();
	}

}
