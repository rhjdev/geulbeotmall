package com.reminder.geulbeotmall.oauth.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.reminder.geulbeotmall.handler.LoginSuccessHandler;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.oauth.model.service.GoogleOAuth2Service;
import com.reminder.geulbeotmall.oauth.model.service.KakaoOAuth2Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
	
	private final KakaoOAuth2Service kakaoOAuth2Service;
	private final GoogleOAuth2Service googleOAuth2Service;
	private final MemberService memberService;
	private final LoginSuccessHandler loginSuccessHandler;
	
	@Autowired
	public OAuth2Controller(KakaoOAuth2Service kakaoOAuth2Service, GoogleOAuth2Service googleOAuth2Service,
			MemberService memberService, LoginSuccessHandler loginSuccessHandler) {
		this.kakaoOAuth2Service = kakaoOAuth2Service;
		this.googleOAuth2Service = googleOAuth2Service;
		this.memberService = memberService;
		this.loginSuccessHandler = loginSuccessHandler;
	}

	@GetMapping("/kakao")
	public ModelAndView signInWithKakao(@RequestParam("code") String code, ModelAndView mv, 
			HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("kakao 회원가입/로그인 요청 : {}", code);
		String access_token = kakaoOAuth2Service.getKakaoAccessToken(code);
		MemberDTO member = kakaoOAuth2Service.getKakaoUserInfo(access_token);
		log.info("member : {}", member);
		boolean isDuplicatedEmail = memberService.checkEmail(member.getEmail()) > 0 ? true : false;
		log.info("isDuplicatedEmail : {}", isDuplicatedEmail);
		if(!isDuplicatedEmail) { //이미 사용 중인 이메일이 아닌 경우에 한하여 신규 회원가입 진행
			mv.addObject("snsName", member.getName());
			mv.addObject("snsEmail", member.getEmail());
			mv.setViewName("/member/signup");
		} else { //등록 회원은 곧바로 로그인 처리
			log.info("로그인 with kakao account");
			MemberDTO snsMember = memberService.findMemberByEmail(member.getEmail());
			log.info("snsMember : {}", snsMember);
			
			UserDetails user = memberService.loadUserByUsername(snsMember.getMemberId());
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
			
			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user, authorities)); //UserDetails 객체로 작성
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			log.info("principal : {}", principal);
			
			if(principal != null) {
				session.setAttribute("signInWithSocialAccount", "kakao"); //success handler redirect 구분용
				loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
			}
			
			mv.setViewName("redirect:/");
		}
		return mv;
	}
	
	@GetMapping("/google")
	public ModelAndView signInWithGoogle(@RequestParam("code") String code, ModelAndView mv, 
			HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("google 회원가입/로그인 요청 : {}", code);
		String access_token = googleOAuth2Service.getGoogleAccessToken(code);
		log.info("access_token : {}", access_token);
		MemberDTO member = googleOAuth2Service.getGoogleUserInfo(access_token);
		log.info("member : {}", member);
		boolean isDuplicatedEmail = memberService.checkEmail(member.getEmail()) > 0 ? true : false;
		log.info("isDuplicatedEmail : {}", isDuplicatedEmail);
		if(!isDuplicatedEmail) { //이미 사용 중인 이메일이 아닌 경우에 한하여 신규 회원가입 진행
			mv.addObject("snsName", member.getName());
			mv.addObject("snsEmail", member.getEmail());
			mv.setViewName("/member/signup");
		} else { //등록 회원은 곧바로 로그인 처리
			log.info("로그인 with kakao account");
			MemberDTO snsMember = memberService.findMemberByEmail(member.getEmail());
			log.info("snsMember : {}", snsMember);
			
			UserDetails user = memberService.loadUserByUsername(snsMember.getMemberId());
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
			
			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user, authorities)); //UserDetails 객체로 작성
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			log.info("principal : {}", principal);
			
			if(principal != null) {
				session.setAttribute("signInWithSocialAccount", "google"); //success handler redirect 구분용
				loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
			}
			
			mv.setViewName("redirect:/");
		}
		return mv;
	}
}
