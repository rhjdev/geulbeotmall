package com.reminder.geulbeotmall.member.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Slf4j
@RestController
public class MessageController {
	
	private final DefaultMessageService messageService; //coolSMS
	
	@Value("${authentication.message.from}")
	private String from;
	
	public MessageController() {
		this.messageService = NurigoApp.INSTANCE.initialize("NCS1QIJZQO0EAISK", "U8HNU1JCUM1NJJNCTZSWWUFPJVYHSJRH", "https://api.coolsms.co.kr");
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
		log.info("response : {}", response);
		
		return number;
	}
}
