package com.reminder.geulbeotmall.member.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.validator.SignUpValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {
	
	private final MemberService memberService;
	private final MessageSource messageSource;
	private final SignUpValidator signUpValidator;
	
	@Autowired
	public MemberController(MemberService memberService, MessageSource messageSource, SignUpValidator signUpValidator) {
		this.memberService = memberService;
		this.messageSource = messageSource;
		this.signUpValidator = signUpValidator;
		/* MessageSource
		 * 1. ContextConfiguration 통해 Bean 등록
		 * 2. classpath 하위에 messages 폴더 및 properties 파일 생성
		 */
	}
	
	/**
	 * 커스터마이징한 유효성 검증 추가
	 */
	@InitBinder
	public void init(WebDataBinder binder) {
		log.info("init binder : {}", binder);
		binder.addValidators(signUpValidator);
	}
	/*
	@PostMapping(value="checkid", produces="application/json; charset=UTF-8")
	@ResponseBody	//ajax 통신에서 JSON 응답을 치르기 위해서는 어노테이션 추가, spring-web의 경우 jackson-databind 등 기본적으로 추가돼 있는 의존성들을 고려하여야 함
	public boolean checkId(@RequestParam("memberId") String memberId) {
		
		log.info("checkId 시작");
		log.info("전달 받은 ID : {}", memberId);
		int count = memberService.checkId(memberId);
		log.info("checkId 결과 : {}", count);
		
		return count;
	}
	*/
	@GetMapping("signup")
	public void signUpForm(Model model) {}
	
	@PostMapping("signup")
	public Object signUpMember(@Validated @ModelAttribute("member") MemberDTO member, BindingResult bindingResult,
			@RequestParam Map<String, String> params, RedirectAttributes rttr, Model model, Locale locale) throws Exception { //BindingResult는 유효성 검사 대상 객체 바로 뒤에 선언
		
		signUpValidator.validate(member, bindingResult);
		
		/* 검증 오류 발생 시 입력 폼으로 리턴 */
		if(bindingResult.hasErrors()) {
			log.info("검증 오류 발생 : {}", bindingResult);
			return "signup";
		}
		
		/* 회원가입 성공 로직 */
		String phone = params.get("phoneA") + params.get("phoneB") + params.get("phoneC");
		String address = params.get("postalCode") + "$" + params.get("address") + "$" + params.get("detailAddress");
		member.setPhone(phone);
		member.setAddress(address);
		memberService.signUpMember(member);
		
		rttr.addFlashAttribute("successMessage", messageSource.getMessage("signUpMember", null, locale));
		
		log.info("성공 로직 실행 완료");
		
		model.addAttribute("member", member);
		
		return "redirect:/";
	}
	
	@GetMapping("signin")
	public void signInForm() {}
	
	@PostMapping("signin")
	public void signInMember() {
		
	}
}
