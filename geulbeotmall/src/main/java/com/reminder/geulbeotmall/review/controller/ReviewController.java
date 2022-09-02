package com.reminder.geulbeotmall.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/review")
public class ReviewController {

	@GetMapping("/write")
	public void getReviewWriter(@RequestParam("no") String orderNo, Model model) {
		log.info("리뷰 작성 요청 주문번호 : {}", orderNo);
	}
}
