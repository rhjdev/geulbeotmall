package com.reminder.geulbeotmall.cs.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.cs.model.service.CSService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/cs")
@SessionAttributes("loginMember")
public class CSController {
	
	private final CSService csService;
	private final MessageSource messageSource;

	@Autowired
	public CSController(CSService csService, MessageSource messageSource) {
		this.csService = csService;
		this.messageSource = messageSource;
	}
	
	/**
	 * 고객센터 메인
	 */
	@GetMapping("main")
	public void getCSPage(HttpSession session, Model model) {
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember != null) {
			List<InquiryDTO> myInquiryList = csService.getMyInquiryList(loginMember);
			model.addAttribute("memberInquiry", myInquiryList);
		}
	}
	
	/**
	 * 1:1 문의 작성
	 */
	@GetMapping("inquiry")
	public void getInquiryWriteForm() {}
	
	@PostMapping(value="inquiry")
	public String writeAInquiry(@ModelAttribute InquiryDTO inquiryDTO, RedirectAttributes rttr, Locale locale) {
		log.info("1:1 문의 작성 요청 : {}", inquiryDTO);
		int result = csService.writeAInquiry(inquiryDTO);
		if(result == 1) {
			rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("inquiryPostedSuccessfully", null, locale));
		} else {
			rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("errorWhilePostingAInquiry", null, locale));
		}
		return "redirect:/cs/main";
	}
	
	/**
	 * 1:1 문의 상세 페이지
	 */
	@GetMapping("inquiry/details")
	public void getInquiryDetails(@RequestParam("no") int inquiryNo) {
		log.info("inquiry details 요청 : {}", inquiryNo);
	}
}
