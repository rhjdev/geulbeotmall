package com.reminder.geulbeotmall.member.controller;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.reminder.geulbeotmall.mail.model.service.MailService;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.oauth.model.service.OAuth2Service;
import com.reminder.geulbeotmall.validator.SignUpValidator;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
@SessionAttributes({"loginMember", "geulbeotCart", "detailOptionNo", "detailOptionQt"})
public class MemberController {
	
	private final MemberService memberService;
	private final OAuth2Service oAuth2Service;
	private final MailService mailService;
	private final MessageSource messageSource;
	private final PasswordEncoder passwordEncoder;
	private final SignUpValidator signUpValidator;
	
	/* MessageSource
	 * 1. ContextConfiguration 통해 Bean 등록
	 * 2. classpath 하위에 messages 폴더 및 properties 파일 생성
	 */
	@Autowired
	public MemberController(MemberService memberService, OAuth2Service oAuth2Service, MailService mailService, 
			MessageSource messageSource, PasswordEncoder passwordEncoder, SignUpValidator signUpValidator) {
		this.memberService = memberService;
		this.oAuth2Service = oAuth2Service;
		this.mailService = mailService;
		this.messageSource = messageSource;
		this.passwordEncoder = passwordEncoder;
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
		mailService.sendVerificationEmail(member);
		return "redirect:/";
	}
	
	/**
	 * 회원가입 후 이메일 인증 및 계정 활성화
	 * @throws NotFoundException 
	 */
	@GetMapping("verify")
	public String signUpEmailVerification(@ModelAttribute("token") String token, RedirectAttributes rttr, Locale locale) throws NotFoundException {
		String email = mailService.verificationEmail(token);
		log.info("인증 email : {}", email);
		
		if(email == null || token == null) { //이미 인증 마친 계정 또는 만료된 토큰으로의 재요청
			log.info("올바르지 않은 접근");
			rttr.addFlashAttribute("verificationMessage", messageSource.getMessage("accessNotAllowed", null, locale));
			return "redirect:/";
		}
		
		boolean isRegistered = memberService.checkEmail(email) > 0 ? true : false;
		if(!isRegistered) {
			log.info("인증 실패");
			rttr.addFlashAttribute("verificationMessage", messageSource.getMessage("emailVerificationFailed", null, locale));
			return "redirect:/";
		} else {
			log.info("인증 성공");
			memberService.activateAccountByEmail(email);
			rttr.addFlashAttribute("verificationMessage", messageSource.getMessage("emailVerifiedSuccessfully", null, locale));
			return "redirect:/member/signin";
		}
	}
	
	/**
	 * 로그인
	 * @return 로그인 폼 또는 에러 페이지로 연결
	 */
	@GetMapping("signin")
	public String signInForm(@AuthenticationPrincipal UserImpl user, Model model, Locale locale) {
		/* 소셜로그인 URL */
		String kakaoUrl = oAuth2Service.getKakaoSignInUrl();
		model.addAttribute("kakaoUrl", kakaoUrl);
		
		if(user != null) { //이미 로그인된 회원이 임의로 재요청하는 경우 denied 페이지로 연결
			model.addAttribute("loginAccessDenied", messageSource.getMessage("loginAccessDenied", null, locale));
			return "/common/denied";
		}
		
		return "/member/signin";
	}
	
	@PostMapping("signin")
	public String signInMember(@RequestParam(required=false) String errorTitle, @RequestParam(required=false) String errorText,
			@RequestParam(required=false) String resetPasswordRequired, HttpServletRequest request, HttpSession session,
			RedirectAttributes rttr, Locale locale) {
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
		 * 임시 비밀번호로 로그인에 성공
		 * : '회원정보수정' 페이지로 이동
		 */
		if(resetPasswordRequired != null ) {
			rttr.addFlashAttribute("resetPasswordRequired", messageSource.getMessage("resetPasswordRequired", null, locale));
			return "redirect:/mypage/change";
		}
		
		/*
		 * 로그인 실패
		 * : LoginFailureHandler로부터 @RequestParam 어노테이션 통해 넘어온 에러메시지 출력
		 */
		if(errorTitle != null) {
			rttr.addFlashAttribute("signInMessageTitle", errorTitle);
			rttr.addFlashAttribute("signInMessageText", errorText);
			return "redirect:/member/signin";
		}
		return uri;
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
	public String findAccountAndPassword(@RequestParam Map<String, Object> params, Model model, RedirectAttributes rttr, Locale locale) {
		log.info("params : {}", params);
		String name = params.get("name").toString();
		String email = params.get("email").toString();
		switch(params.size()) {
			/* 아이디 찾기(이름, 이메일) */
			case 2:
				log.info("find id 요청");
				MemberDTO resultId = memberService.findMemberId(name, email);
				if(resultId == null) {
					rttr.addFlashAttribute("forgotIdMessage", messageSource.getMessage("memberIdNotFound", null, locale));
				} else {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
					String accCreationDate = simpleDateFormat.format(resultId.getAccCreationDate());
					rttr.addFlashAttribute("forgotIdMessage", resultId.getMemberId());
					rttr.addFlashAttribute("accCreationDate", accCreationDate);
				}
				break;
			/* 비밀번호 찾기(아이디, 이름, 이메일) */
			case 3:
				log.info("find pwd 요청");
				String memberId = params.get("memberId").toString();
				
				//1. 임시 비밀번호 발급 및 DB 저장
				String tempPwd = UUID.randomUUID().toString().substring(0, 12); //12자리의 임시 비밀번호 생성
				String encodedTempPwd = passwordEncoder.encode(tempPwd);
				MemberDTO memberDTO = memberService.getMemberDetails(memberId);
				memberDTO.setEmail(email);
				memberDTO.setMemberPwd(encodedTempPwd); //DB 저장용 encoded password
				memberDTO.setTempPwdYn('Y');
				int generated = memberService.generateTempPwd(memberDTO);
				
				//2. 임시 비밀번호 이메일 발송
				if(generated == 1) {
					log.info("임시 비밀번호 요청 memberDTO : {}", memberDTO);
					memberDTO.setMemberPwd(tempPwd); //회원 전달용 plaintext password
					mailService.sendTemporaryPwd(memberDTO);
					rttr.addFlashAttribute("forgotPwdMessage", messageSource.getMessage("temporaryPasswordSent", null, locale));
				} else {
					rttr.addFlashAttribute("forgotPwdMessage", messageSource.getMessage("errorWhileSendingEmail", null, locale));
					return "redirect:/member/forgot";
				}
				break;
		}
		return "redirect:/member/signin";
	}
}
