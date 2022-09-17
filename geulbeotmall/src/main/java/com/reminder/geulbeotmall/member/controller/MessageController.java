package com.reminder.geulbeotmall.member.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@RestController
public class MessageController {
	
	private DefaultMessageService messageService; //coolSMS
	
	@Value("${coolsms.message.from}")
	private String from;
	
	public MessageController(@Value("${coolsms.api.key}") String apiKey, @Value("${coolsms.api.secretKey}") String apiSecretKey) {
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
	}
	
	/**
	 * 휴대폰 본인인증
	 */
	@PostMapping("/member/checkPhone")
	@ResponseBody
	public String authenticateMemberPhone(@RequestParam("phone") String phone) {
		Random random = new Random();
		String number = "";
		for(int i=0; i < 6; i++) {
			String num = Integer.toString(random.nextInt(10));
			number += num;
		}
		Message message = new Message();
		message.setFrom(from);
		message.setTo(phone);
		message.setText("[글벗문구] 본인확인 인증번호는 " + number + "입니다");
		
		SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
		
		return number;
	}
}
