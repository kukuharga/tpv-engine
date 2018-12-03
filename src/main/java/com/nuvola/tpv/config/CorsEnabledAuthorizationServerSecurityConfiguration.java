package com.nuvola.tpv.config;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Import(AuthorizationServerEndpointsConfiguration.class)
@Order(-5)
public class CorsEnabledAuthorizationServerSecurityConfiguration extends AuthorizationServerSecurityConfiguration {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		
		CorsConfigurationSource source = corsConfigurationSource();
		http.cors().and().addFilterBefore(new CorsFilter(source), ChannelProcessingFilter.class);
		
	}

	private CorsConfigurationSource corsConfigurationSource() {
		// UrlBasedCorsConfigurationSource source = new
		// UrlBasedCorsConfigurationSource();
		// CorsConfiguration config = new CorsConfiguration();
		// config.setAllowCredentials(true);
		// config.addAllowedOrigin("*");
		// config.addAllowedHeader("*");
		// config.addAllowedMethod("*");
		// //more config
		// source.registerCorsConfiguration("/**", config);
		// return source;
//		http .requestMatchers() .antMatchers(HttpMethod.OPTIONS, "/**") .and().cors() .and().csrf().disable();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
		configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}