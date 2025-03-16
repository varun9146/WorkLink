//package com.shortlistdcandidate.security.config;
//
//import org.apache.log4j.Logger;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CORSFilter implements WebMvcConfigurer {
//	private static final Logger logger = Logger.getLogger(CORSFilter.class);
//
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		logger.info("Inside CORSFilter: addCorsMappings:");
//		registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//				///.allowedHeaders("*") // Allow all headers
//				.allowedHeaders("Content-Type", "x-session")
//				.allowCredentials(false).maxAge(3600);
//		logger.info("Inside CORSFilter: addCorsMappings:" + registry.toString());
//	}
//}