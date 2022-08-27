package com.reminder.geulbeotmall.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/review")
public class ReviewController {

	@GetMapping("/write")
	public void getReviewWriter() {}
}
