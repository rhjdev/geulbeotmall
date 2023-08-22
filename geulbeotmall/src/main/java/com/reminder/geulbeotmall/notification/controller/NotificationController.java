package com.reminder.geulbeotmall.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.notification.model.dao.NotificationMapper;
import com.reminder.geulbeotmall.notification.model.dto.Notification;
import com.reminder.geulbeotmall.notification.model.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NotificationController {

	private final NotificationService notificationService;
	private final NotificationMapper notificationMapper;
	
	@GetMapping("/notification/{username}")
	public ResponseEntity<List<Notification>> notificationByUsername(@AuthenticationPrincipal UserImpl user) {
		return ResponseEntity.ok().body(notificationService.getAllNotificationByUsername(user.getMemberId()));
	}
	
	@PutMapping("/notification/{username}/read")
	public ResponseEntity<List<Notification>> updateNotificationReadStatusByUsername(@PathVariable String username) {
		notificationService.updateNotificationReadStatusByUsername(username);
		return ResponseEntity.ok().body(notificationService.getAllNotificationByUsername(username)); //수정 후 새롭게 전달
	}
	

	@PutMapping("/notification/{id}/delete")
	public ResponseEntity<List<Notification>> updateNotificationDeleteStatusById(@PathVariable("id") String notificationId) {
		notificationService.updateNotificationDeleteStatusById(notificationId);
		String username = notificationMapper.checkUsernameByNotificationId(notificationId);
		return ResponseEntity.ok().body(notificationService.getAllNotificationByUsername(username)); //수정 후 새롭게 전달
	}
}
