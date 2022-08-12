package com.reminder.geulbeotmall.member.model.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;

/**
 * Spring Security 모듈 UserDetailsService 상속 받아 로그인/로그아웃 로직 처리
 */
public interface MemberService extends UserDetailsService { 
	
	int checkId(String memberId);
	
	int checkEmail(String email);
	
	boolean signUpMember(MemberDTO member) throws Exception;
	
	void updateAccumLoginCount(String username);
	
	void updateLatestLoginDate(String username);
	
	void resetLoginFailedCount(String username);
	
	void updateLoginFailedCount(String username);

	int checkLoginFailedCount(String username);

	void deactivateUsername(String username);

	void addToWishList(String memberId, int optionNo, int refProdNo);

	List<WishListDTO> getMemberWishList(String memberId);
}
