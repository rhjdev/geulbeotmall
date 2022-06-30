package com.reminder.geulbeotmall.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reminder.geulbeotmall.admin.model.service.AdminService;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

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
	public String getMemberList(Model model) {
		log.info("회원 목록 요청");
		
		int total = adminService.getTotalNumber();
		int regular = adminService.getRegularNumber();
		int admin = adminService.getAdminNumber();
		log.info("전체 회원수 : {}", total);
		log.info("일반 회원수 : {}", regular);
		log.info("관리자 수 : {}", admin);
		
		List<MemberDTO> memberList = adminService.getMemberList();
		log.info("회원 목록 조회 완료");
		
		model.addAttribute("total", total);
		model.addAttribute("regular", regular);
		model.addAttribute("admin", admin);
		model.addAttribute("memberList", memberList);
		
		return "admin/member/list";
	}
	
	@GetMapping("/member/details")
	public String getMemberDetails(@RequestParam("id") String memberId, Model model) {
		MemberDTO member = adminService.getMemberDetails(memberId);
		model.addAttribute("member", member);
		return "admin/member/details";
	}
}
