package com.reminder.geulbeotmall.member.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/mypage")
@SessionAttributes({"loginMember", "geulbeotCart"})
public class MypageController {
	
	private final MemberService memberService;
	private final MessageSource messageSource;
	
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public MypageController(MemberService memberService, MessageSource messageSource, PasswordEncoder passwordEncoder) {
		this.memberService = memberService;
		this.messageSource = messageSource;
		this.passwordEncoder = passwordEncoder;
	}
	
	/**
	 * 마이페이지 메인
	 */
	@GetMapping("main")
	public void getMypage(@AuthenticationPrincipal UserImpl user, Model model) { //로그인 된 객체를 UserImpl 타입의 데이터로 관리하고 있으므로 매개변수에 어노테이션과 함께 불러옴
		log.info("로그인 된 유저 : {}", user);
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
	}
	
	/**
	 * 주문/배송목록
	 */
	@GetMapping("order")
	public void getOrderList(@AuthenticationPrincipal UserImpl user, Model model) {
		List<OrderDetailDTO> memberOrderList = memberService.getMemberOrderList(user.getMemberId());
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("memberOrderList", memberOrderList);
	}
	
	/**
	 * 주문/배송 상세정보 조회
	 * @param orderNo
	 */
	@GetMapping("order/details") //details?no={orderNo}
	public void getOrderListDetails(@RequestParam("no") String orderNo, @AuthenticationPrincipal UserImpl user, Model model) {
		log.info("상세조회 요청 주문번호 : {}", orderNo);
		OrderDetailDTO memberOrderDetails = memberService.getMemberOrderDetails(user.getMemberId(), orderNo);
		String method = memberOrderDetails.getPayment().getPaymentMethod();
		switch(method) {
		case "card": method = "신용카드"; break;
		case "trans": method = "실시간계좌이체"; break;
		case "vbank": method = "가상계좌"; break;
		case "phone": method = "휴대폰결제"; break;
		}
		memberOrderDetails.getPayment().setPaymentMethod(method);
		
		List<OrderDTO> orderOptionList = memberService.getOptionListByOrderNo(orderNo);
		
		int totalOrderAmount = memberService.getTotalOrderAmountByOrderNo(orderNo);
		
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("memberOrderDetails", memberOrderDetails);
		model.addAttribute("orderOptionList", orderOptionList);
		model.addAttribute("totalOrderAmount", totalOrderAmount);
	}
	
	/**
	 * 상품리뷰
	 */
	@GetMapping("review")
	public void getReviewList(@AuthenticationPrincipal UserImpl user, Model model) {
		List<OrderDTO> itemList = memberService.getItemsToPostAReview(user.getMemberId());
		List<ReviewDTO> postList = memberService.getMemberReviewPosts(user.getMemberId());
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("itemList", itemList);
		model.addAttribute("postList", postList);
	}
	
	/**
	 * 위시리스트
	 */
	@GetMapping("wishlist")
	public void getWishList(@AuthenticationPrincipal UserImpl user, Model model) {
		List<WishListDTO> memberWishList = memberService.getMemberWishList(user.getMemberId());
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("memberWishList", memberWishList);
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
			memberService.addToWishList(loginMember, Integer.parseInt(optionNoArr[i]));
			count++;
		}
		
		if(optionNoArr.length == count) { result = "성공"; }
		return result;
	}
	
	/**
	 * 위시리스트 선택상품 행 삭제
	 */
	@PostMapping(value="/wishlist/delete", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String removeWishListItem(@RequestBody Map<String, String> param, HttpSession session) {
		String optionNo = param.get("optionNo");
		log.info("삭제 요청 옵션 : {}", optionNo);
		
		int count = 0;
		String result = "";
		String loginMember = (String) session.getAttribute("loginMember");
		List<WishListDTO> memberWishList = memberService.getMemberWishList(loginMember);
		log.info("회원 위시리스트 호출");
		for(int j=0; j < memberWishList.size(); j++) {
			if(Integer.parseInt(optionNo) == memberWishList.get(j).getOptionNo()) {
				count = memberService.deleteItemFromWishList(loginMember, Integer.parseInt(optionNo));
			}
		}
		if(count == 1) {
			result = "succeed";
		}
		return result;
	}
	

	/**
	 * 위시리스트 선택상품목록 삭제
	 */
	@PostMapping(value="/wishlist/deleteAll", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String removeAllItems(HttpServletRequest request, HttpSession session) {
		String[] optionNoArr = request.getParameterValues("arr");
		
		String loginMember = (String) session.getAttribute("loginMember");
		
		int count = 0;
		String result = "";
		for(int i=0; i < optionNoArr.length; i++) {
			String optionNo = optionNoArr[i];
			log.info("삭제 요청 옵션 : {}", optionNo);
			
			List<WishListDTO> memberWishList = memberService.getMemberWishList(loginMember);
			log.info("회원용 장바구니 호출");
			for(int j=0; j < memberWishList.size(); j++) {
				if(Integer.parseInt(optionNo) == memberWishList.get(j).getOptionNo()) {
					count += memberService.deleteItemFromWishList(loginMember, Integer.parseInt(optionNo));
				}
			}
		}
		if(count == optionNoArr.length) {
			result = "succeed";
		}
		return result;
	}

	/**
	 * 적립금사용내역 조회
	 */
	@GetMapping("reserve")
	public void getReserveDetails(@AuthenticationPrincipal UserImpl user, Model model) {
		List<PointDTO> memberPointList = memberService.getReserveDetails(user.getMemberId());
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("memberPointList", memberPointList);
	}
	
	/**
	 * 회원정보수정
	 */
	@GetMapping("change")
	public void getMyAccountInfo(@AuthenticationPrincipal UserImpl user, Model model) {
		MemberDTO memberDTO = memberService.getMemberDetails(user.getMemberId());
		boolean isAuthenticatedMember = memberService.checkIsAuthenticated(user.getMemberId()) == 'Y' ? true : false; //th:value 및 th:disabled 활용 위해 boolean 타입으로 전달
		model.addAttribute("detail", memberDTO);
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
		model.addAttribute("isAuthenticatedMember", isAuthenticatedMember); //현재 본인인증 여부
	}
	
	@PostMapping("change")
	public String changeMyAccountInfo(@RequestParam Map<String, Object> params, @AuthenticationPrincipal UserImpl user, RedirectAttributes rttr, Locale locale) {
		log.info("회원정보수정 요청 : {}", params);
		
		/* A. 현재 비밀번호 일치 여부 확인 => PasswordEncoder matches(raw, encoded) 메소드 활용 */
		if(!passwordEncoder.matches(params.get("memberPwd").toString(), user.getMemberPwd())) {
			rttr.addFlashAttribute("profileUpdateMessage", messageSource.getMessage("incorrectPassword", null, locale));
			return "redirect:/mypage/change";
		/* B. 업데이트 대상 값 토대로 MemberDTO 객체 설정 */
		} else {
			MemberDTO memberDTO = new MemberDTO();
			
			String phone = params.get("phoneA").toString() + params.get("phoneB").toString() + params.get("phoneC").toString();
			String address = params.get("postalCode").toString() + "$" + params.get("address").toString() + "$" + params.get("detailAddress").toString();
			char agreement = params.get("agreement").toString().equals("Y") ? 'Y' : 'N';
			
			if(user.getName() != params.get("name").toString()) { memberDTO.setName(params.get("name").toString()); }
			if(params.get("newPwd").toString() != null) {
				String newPassword = passwordEncoder.encode(params.get("newPwd").toString());
				memberDTO.setMemberPwd(newPassword);
			}
			if(user.getPhone() != phone) { memberDTO.setPhone(phone); }
			if(user.getEmail() != params.get("email").toString()) { memberDTO.setEmail(params.get("email").toString()); }
			if(user.getAddress() != address) { memberDTO.setAddress(address); }
			if(user.getAgreement() != agreement) { memberDTO.setAgreement(agreement); }
			log.info("수정 후 memberDTO : {}", memberDTO);
			
			/* C. 업데이트 후 결과 안내와 함께 페이지 새로고침 */
			int result = memberService.changeMemberInfo(memberDTO);
			if(result == 1) {
				rttr.addFlashAttribute("profileUpdateMessage", messageSource.getMessage("memberProfileUpdatedSuccessfully", null, locale));
			} else {
				rttr.addFlashAttribute("profileUpdateMessage", messageSource.getMessage("errorWhileUpdatingMemberProfile", null, locale));
			}
			return "redirect:/mypage/change";
		}
	}
	
	/**
	 * 회원탈퇴
	 */
	@GetMapping("close")
	public void getCloseForm(@AuthenticationPrincipal UserImpl user, Model model) {
		model.addAttribute("memberId", user.getMemberId());
		model.addAttribute("memberPoint", memberService.getMemberPoint(user.getMemberId())); //현재 보유 적립금
	}
	
	@PostMapping("close")
	public String closeMyAccount(@RequestParam("memberPwd") String memberPwd, @AuthenticationPrincipal UserImpl user, RedirectAttributes rttr, Model model, Locale locale) {
		/* 현재 비밀번호 일치 여부 확인 => PasswordEncoder matches(raw, encoded) 메소드 활용 */
		if(!passwordEncoder.matches(memberPwd, user.getMemberPwd())) {
			rttr.addFlashAttribute("closeAccountMessage", messageSource.getMessage("incorrectPassword", null, locale));
			return "redirect:/mypage/close";
		} else {
			int result = memberService.closeMemberAccount(user.getMemberId());
			if(result == 1) {
				rttr.addFlashAttribute("closeAccountMessage", messageSource.getMessage("memberAccountClosedSuccessfully", null, locale));
				SecurityContextHolder.clearContext(); //로그아웃
			} else {
				rttr.addFlashAttribute("closeAccountMessage", messageSource.getMessage("errorWhileClosingMemberAccount", null, locale));
			}
			return "redirect:/";
		}
	}
}
