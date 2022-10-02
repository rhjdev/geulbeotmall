package com.reminder.geulbeotmall.mail.model.service;

import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.reminder.geulbeotmall.mail.model.dto.MailDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailService {

	/*
	 * [JavaMailSender]
	 * interface JavaMailSender extends MailSender
	 * : MailSender의 하위 인터페이스로, MIME 메시지를 지원
	 * 
	 * [TemplateEngine]
	 * Template Engine 중 Thymeleaf 이용한 이메일 발송
	 * : Template Engine은 template과 data를 통해 동적으로 컨텐츠를 생성하는 방법
	 */
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final RedisService redisService;
	
	@Value("${spring.mail.username}")
	private String from;
	
	@Autowired
	public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine, RedisService redisService) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.redisService = redisService;
	}
	
	/**
	 * 이메일 발송
	 */
	public void sendEmail(MailDTO mailDTO) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper;
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); //mimeMessage, multipart, encoding
			mimeMessageHelper.setTo(mailDTO.getTo());
			mimeMessageHelper.setSubject(mailDTO.getSubject());
			mimeMessageHelper.setText(mailDTO.getText(), true); //text, html
			javaMailSender.send(mimeMessage);
			log.info("email sent : {}", mailDTO.getText());
		} catch (MessagingException e) {
			log.info("failed to send an email : {}", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 신규 회원 인증 메일 전송
	 * @return token
	 */
	public void sendVerificationEmail(MemberDTO memberDTO) {
		UUID uuid = UUID.randomUUID();
		redisService.setTimeout(uuid.toString(), memberDTO.getEmail(), 7);
		
		Context context = new Context();
		context.setVariable("member", memberDTO);
		context.setVariable("token", uuid.toString());
		String content = templateEngine.process("/member/verify", context);
		
		MailDTO mailDTO = MailDTO.builder()
				.to(memberDTO.getEmail())
				.subject("[글벗문구] 신규 회원 이메일 인증")
				.text(content)
				.build();
		sendEmail(mailDTO);
	}
	
	public String verificationEmail(String key) throws NotFoundException {
		String memberToken = redisService.getKey(key);
		if(memberToken == null) redisService.deleteKey(key);
		return memberToken;
	}
	
	/**
	 * '비밀번호 찾기' 요청에 의한 임시 비밀번호 전송
	 * @return 12-digit password
	 */
	public void sendTemporaryPwd(MemberDTO memberDTO) {
		Context context = new Context();
		context.setVariable("name", memberDTO.getName());
		context.setVariable("tempPwd", memberDTO.getMemberPwd());
		String content = templateEngine.process("/member/reset", context);
		
		MailDTO mailDTO = MailDTO.builder()
				.to(memberDTO.getEmail())
				.subject("[글벗문구] 임시 비밀번호 발급")
				.text(content)
				.build();
		sendEmail(mailDTO);
		log.info("mailDTO : {}", mailDTO);
		log.info("from : {}", from);
	}
}
