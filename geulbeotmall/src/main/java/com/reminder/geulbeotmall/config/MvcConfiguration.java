package com.reminder.geulbeotmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
	
	/**
	 * 이미지 저장 경로 설정(application.yml에서 상세 정의)
	 * resourceHandler로 시작하는 URL 요청이 생길 경우 resourceLocation으로 전달
	 * implements WebMvcConfigurer 필요
	 */
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		//상품 등록 중 THUMBNAIL 이미지 요청 처리
		registry.addResourceHandler("/admin/product/add/thumbnail/**")
			    .addResourceLocations("/resources/image/upload/product/thumbnail/");
		
		//기타 이미지 요청 처리
		registry.addResourceHandler("/image/**")
			    .addResourceLocations("/resources/image/upload/");
	}
}
