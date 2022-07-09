package com.reminder.geulbeotmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
	
	/**
	 * 이미지 외부 저장 경로 설정
	 * resourceHandler로 시작하는 URL 요청이 생길 경우 resourceLocation으로 전달
	 * implements WebMvcConfigurer 필요
	 * application.yml에서 상세 정의
	 */
	@Value("${resource.handler}")
	private String resourceHandler;
	
	@Value("${resource.location}")
	private String resourceLocation;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(resourceHandler)
				.addResourceLocations(resourceLocation);
	}
}
