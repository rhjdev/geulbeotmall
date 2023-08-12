package com.reminder.geulbeotmall.member.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.reminder.geulbeotmall.config.GeulbeotmallApplication;

@SpringBootTest
@ContextConfiguration(classes = {GeulbeotmallApplication.class})
public class MemberControllerTests {
	
	@Autowired
	private MemberController memberController;
	private MockMvc mockMvc;
	private PasswordEncoder passwordEncoder;
	
	/*
	@Autowired
	public MemberControllerTests(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	*/
	@Test
	@DisplayName("컨트롤러 의존성 주입 테스트")
	public void testInit() {
		assertNotNull(memberController);
		assertNotNull(mockMvc);
		assertNotNull(passwordEncoder);
		/* 1. 하단의 setUp() 메소드 통해 MockMvc 빌더 객체 설정
		 * 2. @BeforeEach 또는 @BeforeAll 어노테이션 적용하여 테스트 코드 전에 수행하도록 만듦
		 */
	}
	
	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
	}
	
	@Test
	@DisplayName("신규 회원 등록용 컨트롤러 성공 테스트")
	//@Disabled
	public void testSignUpMember() throws Exception {
		
		String password = passwordEncoder.encode("Pass123!");
		
		//given
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); //요청 통해 넘어오는 모든 값은 String
		params.add("memberId", "user01");
		params.add("memberPwd", password);
		params.add("name", "김회원");
		params.add("phone", "01000010001");
		params.add("email", "test@reminder.com");
		params.add("address", "서울특별시");
		params.add("agreement", "Y");
		
		//when & then
		mockMvc.perform(MockMvcRequestBuilders.post("/member/signup").params(params))
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
			   .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
			   .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
			   .andDo(MockMvcResultHandlers.print());
	}
	
}
