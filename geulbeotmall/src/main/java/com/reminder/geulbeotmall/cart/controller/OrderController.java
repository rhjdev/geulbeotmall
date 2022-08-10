package com.reminder.geulbeotmall.cart.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.member.model.dto.UserImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"loginMember", "geulbeotCart"})
public class OrderController {

	@GetMapping("/cart/order")
	public void orderMyCart(@AuthenticationPrincipal UserImpl user, Model model) {
		model.addAttribute("member", user);
	}
	
	@PostMapping("/cart/order")
	@ResponseBody
	public String orderProduct(HttpServletRequest request, HttpSession session, Model model) {
		String[] optionNoArr = request.getParameterValues("orderOptionNo");
		String[] optionQtArr = request.getParameterValues("orderOptionQt");
		
		String result = "";
		
		/* 주문 전 로그인 여부 확인 */
		if(session.getAttribute("loginMember") == null) result = "로그인";
		
		String loginMember = (String) session.getAttribute("loginMember");
		
		return result;
	}
}
