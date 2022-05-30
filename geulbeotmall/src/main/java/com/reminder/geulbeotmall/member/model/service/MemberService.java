package com.reminder.geulbeotmall.member.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

/**
 * Spring Security 모듈 UserDetailsService 상속 받아 로그인/로그아웃 로직 처리
 */
public interface MemberService extends UserDetailsService { 
	
	int checkId(String memberId);
	
	int checkEmail(String email);
	
	boolean signUpMember(MemberDTO member) throws Exception;
	
	void updateFailCountReset(String username);
	
	void updateFailCount(String username);

	int checkLoginFailureCount(String username);

	void deactivateUsername(String username);

}
