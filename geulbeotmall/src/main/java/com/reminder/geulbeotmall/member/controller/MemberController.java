package com.reminder.geulbeotmall.member.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.validator.SignUpValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
@SessionAttributes({"loginMember", "geulbeotCart"})
public class MemberController {
	
	private final MemberService memberService;
	private final ProductMapper productMapper;
	private final MessageSource messageSource;
	private final SignUpValidator signUpValidator;
	private final CartMapper cartMapper;
	
	/* MessageSource
	 * 1. ContextConfiguration 통해 Bean 등록
	 * 2. classpath 하위에 messages 폴더 및 properties 파일 생성
	 */
	@Autowired
	public MemberController(MemberService memberService, ProductMapper productMapper, MessageSource messageSource, SignUpValidator signUpValidator, CartMapper cartMapper) {
		this.memberService = memberService;
		this.productMapper = productMapper;
		this.messageSource = messageSource;
		this.signUpValidator = signUpValidator;
		this.cartMapper = cartMapper;
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
	 */
	@GetMapping("signup")
	public void signUpForm() {}
	
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
		 * 
		 * 결과적으로 request scope 안에 successMessage가 담김
		 * alert를 띄우기 위해서는 main html 안에 작성
		 * */
		rttr.addFlashAttribute("successMessage", messageSource.getMessage("signUpMember", null, locale));
		
		log.info("성공 로직 실행 완료");
		
		return "redirect:/";
	}
	
	/**
	 * 로그인
	 * @return 로그인 폼 또는 에러 페이지로 연결
	 */
	@GetMapping("signin")
	public String signInForm(@AuthenticationPrincipal UserImpl user, @RequestParam(required=false) String errorMessage, Model model) {
		if(user != null) { //이미 로그인된 회원이 임의로 재요청하는 경우 denied 페이지로 연결
			model.addAttribute("errorMessage", "이미 로그인한 상태이거나 접근 권한이 없는 페이지입니다.");
			return "/common/denied";
		}
		return "/member/signin";
	}
	
	@PostMapping("signin")
	public void signInMember(@AuthenticationPrincipal UserImpl user, @RequestParam(required=false) String errorMessage, HttpServletRequest request, HttpSession session, Model model) {
		String uri = request.getHeader("Referer"); //사용자의 이전 경로
		
		String loginMember = (String) session.getAttribute("loginMember");
		if(user != null) { //이미 로그인된 회원이 임의로 재요청하는 경우 에러메시지 전달
			model.addAttribute("errorMessage", "이미 로그인한 상태이거나 접근 권한이 없는 페이지입니다.");
		}
		if(loginMember == null) {
			if(uri != null && !(uri.contains("/signin"))) { //돌아가야 할 이전 경로가 존재하고, 사용자가 직접 로그인페이지를 요청한 것이 아닌 경우
				request.getSession().setAttribute("prevPage", uri); //session상에 저장하여 LoginSuccessHandler 통해 처리
			}
		}
		model.addAttribute("errorMessage", errorMessage);
	}
	
	/**
	 * 마이페이지
	 */
	@GetMapping("mypage")
	public void mypage(@AuthenticationPrincipal UserImpl user) {
		//로그인 된 객체를 UserImpl 타입의 데이터로 관리하고 있으므로 매개변수에 어노테이션과 함께 불러옴
		log.info("로그인 된 유저 : {}", user);
	}
	

	/**
	 * 위시리스트 찜하기 추가
	 */
	@PostMapping(value="/wishlist/add", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String addToWishList(@ModelAttribute("loginMember") String loginMember, HttpServletRequest request, Model model) {
		String[] optionNoArr = request.getParameterValues("arr");
		log.info("optionNoArr : {}", optionNoArr.length);
		
		String result = "";
		
		List<WishListDTO> memberWishList = memberService.getMemberWishList(loginMember);
		
		int count = 0;
		for(int i=0; i < optionNoArr.length; i++) {
			for(int j=0; j < memberWishList.size(); j++) { //현재 위시리스트와 비교하여 찜하기 중복 여부 확인
				if(memberWishList.get(j).getOptionNo() == Integer.parseInt(optionNoArr[i])) {
					return result;
				}
			}
			int prodNo = productMapper.searchProdNoByOptionNo(Integer.parseInt(optionNoArr[i]));
			memberService.addToWishList(loginMember, Integer.parseInt(optionNoArr[i]), prodNo);
			count++;
		}
		
		if(optionNoArr.length == count) { result = "성공"; }
		return result;
	}
	
	/**
	 * 로그아웃
	 */
	@GetMapping("signout")
	public void signOut(SessionStatus status) {
		status.setComplete(); //@SessionAttributes 어노테이션과 함께 session에 저장했던 속성 삭제
	}
}
