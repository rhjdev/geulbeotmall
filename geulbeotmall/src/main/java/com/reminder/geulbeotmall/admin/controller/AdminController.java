package com.reminder.geulbeotmall.admin.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reminder.geulbeotmall.admin.model.dto.SuspDTO;
import com.reminder.geulbeotmall.admin.model.service.AdminService;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.paging.model.dto.PageDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
	
	private final AdminService adminService;
	
	@Autowired
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@GetMapping("/dashboard")
	public String getDashboard() {
		return "admin/dashboard";
	}
	
	@GetMapping("/member/list")
	public void getMemberList(@Valid @ModelAttribute("criteria") Criteria criteria, BindingResult bindingResult, HttpServletRequest request, Model model) {
		log.info("회원 목록 요청");
		
		int total = adminService.getTotalNumber(criteria);
		int regular = adminService.getRegularNumber(criteria);
		int admin = adminService.getAdminNumber(criteria);
		int closed = adminService.getClosedNumber();
		log.info("전체 회원수 : {}", total);
		log.info("일반 회원수 : {}", regular);
		log.info("관리자 수 : {}", admin);
		
		List<MemberDTO> memberList = adminService.getMemberList(criteria);
		List<MemberDTO> memberOnly = adminService.getMemberOnly(criteria);
		List<MemberDTO> adminOnly = adminService.getAdminOnly(criteria);
		List<MemberDTO> closedOnly = adminService.getClosedOnly();
		log.info("회원 목록 조회 완료");
		
		model.addAttribute("total", total);
		model.addAttribute("regular", regular);
		model.addAttribute("admin", admin);
		model.addAttribute("closed", closed);
		model.addAttribute("memberList", memberList);
		model.addAttribute("memberOnly", memberOnly);
		model.addAttribute("adminOnly", adminOnly);
		model.addAttribute("closedOnly", closedOnly);
		model.addAttribute("pageMaker", new PageDTO(adminService.getTotalNumber(criteria), 10, criteria));
	}
	
	@GetMapping("/member/details")
	public String getMemberDetails(@RequestParam("id") String memberId, Model model) {
		MemberDTO detail = adminService.getMemberDetails(memberId);
		List<SuspDTO> susp = adminService.getSuspDetails(memberId);
		int suspCount = adminService.getSuspCount(memberId);
		model.addAttribute("detail", detail);
		model.addAttribute("susp", susp);
		model.addAttribute("suspCount", suspCount);
		return "admin/member/details";
	}
	
	@PostMapping(value="/member/manageAuth", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String changeAuth(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		log.info("권한 수정 시작");
		String optValue = params.get("optValue").toString();
		int selected = Integer.parseInt(optValue);
		String[] idList = request.getParameterValues("arr");
		int count = 0;
		String result = "";
		
		for(int i=0; i < idList.length; i++) {
			int current = adminService.searchAuthById(idList[i]);
			log.info("현재 권한 수(1 : 일반회원, 2 : 관리자) : {}", current);
			
			if(current == 2 && selected == 2) {
				result = idList[i] + "(은)는 이미 관리자입니다";
				log.info(result);
			} else if(current == 1 && selected == 1) {
				result = idList[i] + "(은)는 이미 일반회원입니다";
				log.info(result);
			} else if(current == 2 && selected == 1) { //현재 관리자에서 일반회원으로 변경
				count += adminService.deleteAuthAsAdmin(idList[i]);
			} else { //현재 일반회원에서 관리자로 변경
				count += adminService.insertAuthAsAdmin(idList[i]);
			}
			
			if(idList.length == count) {
				result = "성공";
				log.info(result);
			}
		}
		return result;
	}
	
	@PostMapping(value="/member/suspendAcc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String updateAccSuspension(@RequestParam Map<String, Object> params) {
		String memberId = params.get("memberId").toString();
		String accSuspDesc = params.get("accSuspDesc").toString();
		int resultA = adminService.updateAccSuspension(memberId);
		int resultB = adminService.insertAccSuspension(memberId, accSuspDesc);
		
		String result = "";
		if(resultA == 1 && resultB == 1) {
			result = "성공";
		}
		return result;
	}
	
	@PostMapping(value="/member/activateAcc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String updateAccActivation(HttpServletRequest request) {
		String[] idList = request.getParameterValues("arr");
		int count = 0;
		String result = "";
		
		for(int i=0; i < idList.length; i++) {
			adminService.updateAccActivation(idList[i]);
			count++;
		}
		
		if(idList.length == count) {
			result = "성공";
		}
		return result;
	}
	
	@GetMapping("/order/list")
	public void getOrderList(@Valid @ModelAttribute("criteria") Criteria criteria, Model model) {
		log.info("주문/배송 목록 요청");
		
		List<OrderDetailDTO> totalOrderList = adminService.getTotalOrderList(criteria);
		List<OrderDetailDTO> preparingOnly = adminService.getPreparingOnly(criteria);
		List<OrderDetailDTO> deliveringOnly = adminService.getDeliveringOnly(criteria);
		List<OrderDetailDTO> completedOnly = adminService.getCompletedOnly(criteria);
		log.info("주문/배송 목록 조회 완료");
		
		model.addAttribute("total", totalOrderList.size());
		model.addAttribute("preparing", preparingOnly.size());
		model.addAttribute("delivering", deliveringOnly.size());
		model.addAttribute("completed", completedOnly.size());
		model.addAttribute("totalOrderList", totalOrderList);
		model.addAttribute("preparingOnly", preparingOnly);
		model.addAttribute("deliveringOnly", deliveringOnly);
		model.addAttribute("completedOnly", completedOnly);
		model.addAttribute("pageMaker", new PageDTO(adminService.getTotalNumber(criteria), 10, criteria));
	}

	@PostMapping(value="/order/manageDeliveryStatus", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String changeDlvrStatus(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		log.info("배송상태 변경 시작");
		String dlvrStatus = params.get("dlvrValue").toString();
		String[] orderList = request.getParameterValues("arr");
		boolean isCommited = false;
		String result = "";
		
		for(int i=0; i < orderList.length; i++) {
			isCommited = adminService.updateDeliveryStatus(dlvrStatus, orderList[i]);
		}
		
		if(isCommited) {
			result = "succeed";
		}
		return result;
	}
	
	@GetMapping("/order/details")
	public String getOrderDetails(@RequestParam("no") String orderNo, Model model) {
		OrderDetailDTO detail = adminService.getOrderDetailsByOrderNo(orderNo);
		String method = detail.getPayment().getPaymentMethod();
		switch(method) {
			case "card": method = "신용카드"; break;
			case "trans": method = "실시간계좌이체"; break;
			case "vbank": method = "가상계좌"; break;
			case "phone": method = "휴대폰결제"; break;
		}
		detail.getPayment().setPaymentMethod(method);
		model.addAttribute("detail", detail);
		return "admin/order/details";
	}
}
