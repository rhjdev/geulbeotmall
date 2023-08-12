package com.reminder.geulbeotmall.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.reminder.geulbeotmall.member.model.dao.MemberMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
	
	private MemberMapper memberMapper;
	
	@Autowired
	public LoginFailureHandler(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("onAuthenticationFailure");
		
		String errorTitle = "";
		String errorText = "";
		
		log.info("발생한 exception 클래스 : {}", exception.getClass());
		
		if(exception instanceof BadCredentialsException) {
			String username = request.getParameter("username");
			log.info("username : {}", username);
			int currentFailedCount = memberMapper.findMemberById(username).getLoginFailedCount()+1;
			if(currentFailedCount < 5) {
				memberMapper.updateLoginFailedCount(username);
				errorTitle = "아이디 또는 비밀번호가 일치하지 않습니다";
				errorText = "5회 이상 실패 시 계정이 잠금 처리됩니다";
			} else {
				memberMapper.deactivateUsername(username);
				errorTitle = "5회 이상 로그인에 실패하여 잠금된 계정입니다";
				errorText = "아이디/비밀번호 찾기를 이용하세요";
			}
		} else if(exception instanceof UsernameNotFoundException) {
			errorTitle = "존재하지 않는 계정입니다";
			errorText = "";
		} else if(exception instanceof LockedException) {
			errorTitle = "잠금 처리된 계정입니다";
			errorText = "아이디/비밀번호 찾기를 이용하거나 관리자에게 문의하세요";
		} else {
			errorTitle = "로그인 요청을 처리할 수 없습니다";
			errorText = "알 수 없는 오류로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요";
			
			if(exception.getMessage().indexOf("비활성화") > 0) {
				errorTitle = "이메일 인증 전 비활성화 상태인 계정입니다";
				errorText = "본인인증메일을 확인하세요";
			}
		}
		
		log.info("exception.getMessage() : {}", exception.getMessage());
		log.info("커스텀 exception 메시지 : {}", errorTitle + errorText);
		
		request.getRequestDispatcher("/member/signin?errorTitle=" + errorTitle + "&errorText=" + errorText).forward(request, response);
	}
}
