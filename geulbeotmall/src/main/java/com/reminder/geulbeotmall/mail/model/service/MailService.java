package com.reminder.geulbeotmall.mail.model.service;

import java.util.Date;

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
	
	@Value("${spring.mail.username}")
	private String from;
	
	@Autowired
	public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}
	
	/**
	 * 이메일 발송
	 */
	public void sendVerificationEmail(MailDTO mailDTO) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper;
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); //mimeMessage, multipart, encoding
			mimeMessageHelper.setTo(mailDTO.getTo());
			mimeMessageHelper.setSubject(mailDTO.getSubject());
			mimeMessageHelper.setText(mailDTO.getText(), true); //text, html
			javaMailSender.send(mimeMessage);
			log.info("verification email sent : {}", mailDTO.getText());
		} catch (MessagingException e) {
			log.info("failed to send a verification email : {}", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * '비밀번호 찾기' 요청에 의한 임시 비밀번호 전송
	 * @return 12-digit password
	 */
	public void sendTemporaryPwd(MemberDTO memberDTO) {
		Context context = new Context();
		context.setVariable("name", memberDTO.getName());
		context.setVariable("tempPwd", memberDTO.getMemberPwd());
		String content = templateEngine.process("/member/mail", context);
		
		MailDTO mailDTO = MailDTO.builder()
				.to(memberDTO.getEmail())
				.subject("[글벗문구] 임시 비밀번호 발급")
				.text(content)
				.build();
		//sendVerificationEmail(mailDTO);
		log.info("mailDTO : {}", mailDTO);
		log.info("from : {}", from);
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper;
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); //mimeMessage, multipart, encoding
			mimeMessageHelper.setFrom(from);
			mimeMessageHelper.setTo(mailDTO.getTo());
			mimeMessageHelper.setSubject(mailDTO.getSubject());
			mimeMessageHelper.setText(mailDTO.getText(), true); //text, html
			mimeMessageHelper.setSentDate(new Date());
			log.info("mimeMessage : {}", mimeMessage);
			javaMailSender.send(mimeMessage);
			log.info("verification email sent : {}", mailDTO.getText());
		} catch (MessagingException e) {
			log.info("failed to send a verification email : {}", e);
			e.printStackTrace();
		}
	}
}
