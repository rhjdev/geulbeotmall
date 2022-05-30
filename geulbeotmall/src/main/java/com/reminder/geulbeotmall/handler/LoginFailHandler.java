package com.reminder.geulbeotmall.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.reminder.geulbeotmall.member.model.service.MemberService;

public class LoginFailHandler implements AuthenticationFailureHandler {
	
	private MemberService memberService;
	private String loginId;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String errorMessage = exception.getMessage();
		
		if(exception instanceof BadCredentialsException) {
			String username = request.getParameter(loginId);
			updateFailCount(username);
			errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다";
		}
		
		request.getRequestDispatcher("/member/signin?errorMessage=" + errorMessage).forward(request, response);
	}
	
	public String getLoginId() {
		return loginId;
	}
	
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	protected void updateFailCount(String username) {
		memberService.updateFailCount(username);
		int count = memberService.checkLoginFailureCount(username);
		if(count == 3) {
			memberService.deactivateUsername(username);
		}
	}
}
