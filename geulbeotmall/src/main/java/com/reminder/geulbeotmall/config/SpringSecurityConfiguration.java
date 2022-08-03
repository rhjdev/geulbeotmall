package com.reminder.geulbeotmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.reminder.geulbeotmall.handler.LoginFailureHandler;
import com.reminder.geulbeotmall.handler.LoginSuccessHandler;
import com.reminder.geulbeotmall.member.model.service.MemberService;

@EnableWebSecurity //스프링 시큐리티 설정 활성화
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter { //Adapter 객체는 필요한 옵션들만 선택적으로 오버라이딩하여 사용 가능
	
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	
	@Autowired
	public SpringSecurityConfiguration(MemberService memberService, PasswordEncoder passwordEncoder, LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler) {
		this.memberService = memberService;
		this.passwordEncoder = passwordEncoder;
		this.loginSuccessHandler = loginSuccessHandler;
		this.loginFailureHandler = loginFailureHandler;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/image/**");
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
			.authorizeRequests()
				.antMatchers("/order/**").authenticated()
				.antMatchers(HttpMethod.GET, "/order/**").hasRole("MEMBER")
				.antMatchers(HttpMethod.POST, "/order/**").hasRole("ADMIN")
				.antMatchers("/admin/member/**").hasRole("ADMIN")
				.antMatchers("/admin/product/**").hasRole("ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/member/mypage").hasRole("MEMBER")
				.anyRequest().permitAll()
			/* 로그인 */
			.and()
				.formLogin()															//로그인 설정
				.loginPage("/member/signin")											//로그인 페이지
				.successForwardUrl("/")													//성공 시 랜딩 페이지
				.failureHandler(loginFailureHandler)									//실패 시 핸들러, AuthenticationFailureHandler를 implements한 해당 클래스에서 @Component 선언
				.successHandler(loginSuccessHandler)									//성공 시 핸들러, AuthenticationSuccessHandler를 implements한 해당 클래스에서 @Component 선언
				.usernameParameter("username")
				.passwordParameter("password")
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
				.accessDeniedPage("/common/denied") 									//권한 없는 등 인가되지 않을 시 이동 페이지
			/* 에디터 적용 중 X-Frame-Options로 인한 오류 처리
			 * Refused to display in a frame because it set 'X-Frame-Options' to 'deny'. */
			.and()
				.headers()
				.frameOptions().sameOrigin();
	}
}
