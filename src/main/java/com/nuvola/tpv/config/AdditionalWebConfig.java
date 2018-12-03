package com.nuvola.tpv.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by nydiarra on 06/05/17.
 */
@Configuration
@Order(-2)	
public class AdditionalWebConfig {
	/**
	 * Allowing all origins, headers and methods here is only intended to keep this
	 * example simple. This is not a default recommended configuration. Make
	 * adjustments as necessary to your use case.
	 *
	 */
	
//	@Value("${spring.data.mongodb.host}")
//	private String mongoHost;
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
	public FilterRegistrationBean simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	 
//	@Bean()
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
//			}
//		};
//	}

}
