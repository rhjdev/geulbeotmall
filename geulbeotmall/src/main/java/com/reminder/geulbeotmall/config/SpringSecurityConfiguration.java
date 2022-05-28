package com.reminder.geulbeotmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.reminder.geulbeotmall.handler.LoginFailHandler;
import com.reminder.geulbeotmall.member.model.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter { //Adapter 객체는 필요한 옵션들만 선택적으로 오버라이딩하여 사용 가능
	
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public SpringSecurityConfiguration(MemberService memberService, PasswordEncoder passwordEncoder) {
		this.memberService = memberService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(memberService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			/* 요청 권한 체크 */
			.csrf().disable()
			.authorizeHttpRequests()
				.antMatchers("/order/**").authenticated()
				.antMatchers(HttpMethod.GET, "/order/**").hasRole("MEMBER")
				.antMatchers(HttpMethod.POST, "order/**").hasRole("ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/member/mypage").hasRole("MEMBER")
				.anyRequest().permitAll()
			/* 로그인 */
			.and()
				.formLogin()															//로그인 설정
				.loginPage("/member/signin")											//로그인 페이지
				.successForwardUrl("/")													//성공 시 랜딩 페이지
				.failureHandler(loginFailHandler())										//실패 시 핸들러(AuthenticationFailureHandler 타입의 클래스 먼저 설정, 하단에 bean으로 등록, 해당 bean 호출)
			/* 로그아웃 */
			.and()
				.logout()																//로그아웃 설정
				.logoutRequestMatcher(new AntPathRequestMatcher("/member/signout"))		//로그아웃 주소
				.deleteCookies("JSESSIONID")											//JSESSIONID 쿠키 삭제
				.invalidateHttpSession(true)											//세션 만료
				.logoutSuccessUrl("/")													//성공 시 랜딩 페이지
			/* 인증/인가 */
			.and()
				.exceptionHandling()													//예외 처리
				.accessDeniedPage("/common/denied");									//권한 없는 등 인가되지 않을 시 이동 페이지
	}
	
	/**
	 * 로그인 실패 핸들러 
	 */
	@Bean
	public LoginFailHandler loginFailHandler() {
		return new LoginFailHandler();
	}

}
