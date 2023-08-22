package com.reminder.geulbeotmall.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.notification.model.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SseController {

	private final SseService sseService;

	/**
	 * 로그인 시 SSE 구독(연결)
	 */
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserImpl user,
			@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
		return ResponseEntity.ok(sseService.subscribe(user.getMemberId(), lastEventId));
	}
}
