package com.reminder.geulbeotmall.handler;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.member.model.dao.MemberMapper;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SessionAttributes("loginMember")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private MemberMapper memberMapper;
	
	@Autowired
	public LoginSuccessHandler(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		log.info("onAuthenticationSuccess");
		
		UserImpl loginMember = (UserImpl) authentication.getPrincipal();
		String username = loginMember.getMemberId();
		
		request.getSession().setAttribute("loginMember", username); //session에 현재 로그인한 회원 정보 저장
		
		memberMapper.resetLoginFailedCount(username); //현재까지 기록된 로그인 실패 횟수 초기화
		memberMapper.updateAccumLoginCount(username); //누적 로그인 횟수 증가
		memberMapper.updateLatestLoginDate(username); //최근 로그인 일시 업데이트
		
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		log.info("ROLE : {}", roles);
		
		if(roles.contains("ROLE_ADMIN")) { //관리자는 로그인 시 대시보드로 기본 이동
			response.sendRedirect("/admin/dashboard");
		} else {
			response.sendRedirect("/");
		}
	}
}
