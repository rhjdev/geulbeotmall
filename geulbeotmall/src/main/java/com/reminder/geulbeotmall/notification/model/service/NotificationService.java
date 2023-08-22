package com.reminder.geulbeotmall.notification.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.geulbeotmall.notification.model.dao.NotificationMapper;
import com.reminder.geulbeotmall.notification.model.dto.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationMapper notificationMapper;
	private final SseService sseService;

	/**
	 * [DB 연동]전체 알림 조회
	 */
	@Transactional(readOnly = true)
	public List<Notification> getAllNotificationByUsername(String username) {
		List<Notification> notificationList = notificationMapper.getAllNotificationByUsername(username);
		return notificationList.stream().map(Notification::of).collect(Collectors.toList());
	}
	
	/**
	 * [DB 연동]전체 알림 읽음 상태 업데이트
	 */
	@Transactional
	public void updateNotificationReadStatusByUsername(String username) {
		notificationMapper.updateNotificationReadStatusByUsername(username);
	}
	
	/**
	 * [DB 연동]단일 알림 삭제 상태 업데이트
	 */
	@Transactional
	public void updateNotificationDeleteStatusById(String notificationId) {
		notificationMapper.updateNotificationDeleteStatusById(notificationId);
	}
	
	/**
	 * [DB 연동]다수 알림 전송
	 */
	@Transactional
	public void sendNotifications(List<Notification> notificationList) {
		notificationList.forEach(notification -> {
			notificationMapper.insertNotification(notification); //DB 저장
			sseService.send(notification.getReceiver(), notification.getContent(), notification.getNotificationType(), notification.getUrl());
		});
	}
	
	/**
	 * [DB 연동]단일 알림 전송
	 */
	@Transactional
	public void sendNotification(Notification notification) {
		notificationMapper.insertNotification(notification); //DB 저장
		sseService.send(notification.getReceiver(), notification.getContent(), notification.getNotificationType(), notification.getUrl());
	}
}
