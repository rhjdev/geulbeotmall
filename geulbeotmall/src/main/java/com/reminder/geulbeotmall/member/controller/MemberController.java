package com.reminder.geulbeotmall.member.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.validator.SignUpValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
@SessionAttributes({"loginMember", "geulbeotCart", "detailOptionNo", "detailOptionQt"})
public class MemberController {
	
	private final MemberService memberService;
	private final MessageSource messageSource;
	private final SignUpValidator signUpValidator;
	
	/* MessageSource
	 * 1. ContextConfiguration 통해 Bean 등록
	 * 2. classpath 하위에 messages 폴더 및 properties 파일 생성
	 */
	@Autowired
	public MemberController(MemberService memberService, MessageSource messageSource, SignUpValidator signUpValidator) {
		this.memberService = memberService;
		this.messageSource = messageSource;
		this.signUpValidator = signUpValidator;
	}
	
	/**
	 * 커스터마이징한 유효성 검증 추가
	 */
//	@InitBinder
//	public void init(WebDataBinder binder) {
//		log.info("init binder : {}", binder);
//		binder.addValidators(signUpValidator);
//	}
	
	/**
	 * 아이디 중복 검사
	 * @return 중복된 아이디 개수
	 */
	@PostMapping(value="/checkId", produces="application/json; charset=UTF-8")
	@ResponseBody	//ajax 통신에서 JSON 응답을 치르기 위해서는 어노테이션 추가, spring-web의 경우 jackson-databind 등 기본적으로 추가돼 있는 의존성들을 고려하여야 함
	public int checkId(String memberId) {
		
		log.info("checkId 시작");
		log.info("전달 받은 ID : {}", memberId);
		int result = memberService.checkId(memberId);
		log.info("checkId 결과 : {}", result);
		
		return result;
	}
	
	/** 
	 * 이메일 중복 검사
	 * @return 중복된 이메일 개수
	 */
	@PostMapping(value="/checkEmail", produces="application/json; charset=UTF-8")
	@ResponseBody
	public int checkEmail(String email) {
		
		log.info("checkEmail 시작");
		log.info("전달 받은 Email : {}", email);
		int result = memberService.checkEmail(email);
		log.info("checkEmail 결과 : {}", result);
		
		return result;
	}
	
	/**
	 * 회원가입
	 * @return 회원가입 폼 또는 에러 페이지로 연결
	 */
	@GetMapping("signup")
	public String signUpForm(@AuthenticationPrincipal UserImpl user, Model model) {
		if(user != null) { //이미 로그인된 회원이 임의로 재요청하는 경우 denied 페이지로 연결
			model.addAttribute("errorMessage", "이미 로그인한 상태이거나 접근 권한이 없는 페이지입니다.");
			return "/common/denied";
		}
		return "/member/signup";
	}
	
	@PostMapping("signup")
	public Object signUpMember(@Validated @ModelAttribute("member") MemberDTO member, BindingResult bindingResult,
			@RequestParam Map<String, String> params, RedirectAttributes rttr, Model model, Locale locale) throws Exception { //BindingResult는 유효성 검사 대상 객체 바로 뒤에 선언
		/*
		signUpValidator.validate(member, bindingResult);
		
		if(bindingResult.hasErrors()) { //검증 오류 발생 시 입력 폼으로 리턴
			log.info("검증 오류 발생 : {}", bindingResult);
			return "signup";
		}
		*/
		
		/* 회원가입 성공 로직 */
		String phone = params.get("phoneA") + params.get("phoneB") + params.get("phoneC");
		String address = params.get("postalCode") + "$" + params.get("address") + "$" + params.get("detailAddress");
		member.setPhone(phone);
		member.setAddress(address);
		memberService.signUpMember(member);
		
		/* 리다이렉트 시에는 요청이 새로 생겨나는 것이므로 RedirectAttributes 사용 
		 * 메시지 목록을 리터럴리하게 쓰지 않고 따로 목록화 해서 관리되도록 ContextConfiguration에 bean으로 등록
		 * 상단에 MessageSource 타입에 대하여 의존성 주입
		 * 결과적으로 request scope 안에 successMessage가 담기는 것
		 */
		rttr.addFlashAttribute("signUpMessage", messageSource.getMessage("signUpMember", null, locale));
		log.info("성공 로직 실행 완료");
		return "redirect:/";
	}
	
	/**
	 * 로그인
	 * @return 로그인 폼 또는 에러 페이지로 연결
	 */
	@GetMapping("signin")
	public String signInForm(@AuthenticationPrincipal UserImpl user, Model model, Locale locale) {
		if(user != null) { //이미 로그인된 회원이 임의로 재요청하는 경우 denied 페이지로 연결
			model.addAttribute("loginAccessDenied", messageSource.getMessage("loginAccessDenied", null, locale));
			return "/common/denied";
		}
		return "/member/signin";
	}
	
	@PostMapping("signin")
	public void signInMember(@RequestParam(required=false) String errorTitle, @RequestParam(required=false) String errorText,
			@AuthenticationPrincipal UserImpl user, HttpServletRequest request, HttpSession session,
			RedirectAttributes rttr, Model model, Locale locale) {
		/* 
		 * [Handler 이동 전 요청별 확인]
		 * who : 비로그인 상태의 회원
		 * what : 1)상품상세페이지 > '바로주문' 요청 => @SessionAttributes 어노테이션 활용해 선택값을 session상에 임시 저장
		 * 		  2)이전 경로가 존재 => 이를 session상에 임시 저장
		 */
		String[] detailOptionNo = request.getParameterValues("orderOptionNo");
		String[] detailOptionQt = request.getParameterValues("orderOptionQt");
		session.setAttribute("detailOptionNo", detailOptionNo);
		session.setAttribute("detailOptionQt", detailOptionQt);
		
		String uri = request.getHeader("Referer"); //사용자의 이전 경로
		
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember == null) {
			if(uri != null && !(uri.contains("/signin"))) { //돌아가야 할 이전 경로가 존재하고, 사용자가 직접 로그인페이지를 요청한 것이 아닌 경우
				request.getSession().setAttribute("prevPage", uri); //이전 경로를 session상에 저장하여 LoginSuccessHandler 통해 처리
			}
		}
		
		/*
		 * 로그인 실패
		 * : LoginFailureHandler로부터 @RequestParam 어노테이션 통해 넘어온 에러메시지 출력
		 */
		if(errorTitle != null) {
//			rttr.addFlashAttribute("signInMessageTitle", errorTitle);
//			rttr.addFlashAttribute("signInMessageText", errorText);
			model.addAttribute("signInMessageTitle", errorTitle);
			model.addAttribute("signInMessageText", errorText);
		}
	}
		
	/**
	 * 로그아웃
	 */
	@GetMapping("signout")
	public void signOut(SessionStatus status) {
		status.setComplete(); //@SessionAttributes 어노테이션과 함께 session에 저장했던 속성 삭제
	}
	

	/**
	 * 휴대폰 본인인증 결과 반영
	 */
	@PostMapping("authenticated")
	@ResponseBody
	public String authenticatedMember(@RequestParam("result") String authPhoneYn, @AuthenticationPrincipal UserImpl user) {
		boolean isCommited = memberService.updateAuthentication(user.getMemberId(), authPhoneYn.charAt(0));
		return isCommited == true ? "succeed" : "fail";
	}
	
	/**
	 * 아이디/비밀번호 찾기
	 */
	@GetMapping("forgot")
	public void getfindForm() {}
	
	@PostMapping("forgot")
	public void findAccountAndPassword(@RequestParam Map<String, Object> params) {
		log.info("params : {}", params);
		String name = params.get("name").toString();
		String email = params.get("email").toString();
		switch(params.size()) {
			case 2:
				log.info("find id 요청");
				String resultId = memberService.findMemberId(name, email);
				break;
			case 3:
				log.info("find pwd 요청");
				String memberId = params.get("memberId").toString();
//				String token = RandomString.make(30);
//				int result = memberService.createTempPassword(memberId, name, email);
				break;
		}
	}
}
