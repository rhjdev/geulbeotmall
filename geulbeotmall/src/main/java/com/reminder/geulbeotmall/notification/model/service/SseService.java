package com.reminder.geulbeotmall.notification.model.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.reminder.geulbeotmall.notification.model.dto.Notification;
import com.reminder.geulbeotmall.notification.model.dto.NotificationType;
import com.reminder.geulbeotmall.notification.model.repository.SseRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; //60분
	
	private final SseRepositoryImpl sseRepository;

	/**
	 * [SSE 통신]연결
	 */
	public SseEmitter subscribe(String username, String lastEventId) {
		String emitterId = username + "_" + System.currentTimeMillis();
		
		SseEmitter sseEmitter = sseRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
		log.info("new emitter added : {}", sseEmitter);
		log.info("lastEventId : {}", lastEventId);
		
		/* 상황별 emitter 삭제 처리 */
		sseEmitter.onCompletion(() -> sseRepository.deleteEmitterById(emitterId)); //완료 시, 타임아웃 시, 에러 발생 시
		sseEmitter.onTimeout(() -> sseRepository.deleteEmitterById(emitterId));
		sseEmitter.onError((e) -> sseRepository.deleteEmitterById(emitterId));
		
		/* 503 Service Unavailable 방지용 dummy event 전송 */
		send(sseEmitter, emitterId, createDummyNotification(username));
		
		/* client가 미수신한 event 목록이 존재하는 경우 */
		if(!lastEventId.isEmpty()) { //client가 미수신한 event가 존재하는 경우 이를 전송하여 유실 예방
			Map<String, Object> eventCaches = sseRepository.findAllEventCacheStartsWithUsername(username); //id에 해당하는 eventCache 조회
			eventCaches.entrySet().stream() //미수신 상태인 event 목록 전송
					.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
					.forEach(entry -> emitEventToClient(sseEmitter, entry.getKey(), entry.getValue()));
		}
		return sseEmitter;
	}
	
	/**
	 * [SSE 통신]specific user에게 알림 전송
	 */
	public void send(String receiver, String content, String type, String url) {
		Notification notification = createNotification(receiver, content, type, url);
		/* 로그인한 client의 sseEmitter 전체 호출 */
		Map<String, SseEmitter> sseEmitters = sseRepository.findAllEmitterStartsWithUsername(receiver);
		sseEmitters.forEach(
			(key, sseEmitter) -> {
				log.info("key, notification : {}, {}", key, notification);
				sseRepository.saveEventCache(key, notification); //저장
				emitEventToClient(sseEmitter, key, notification); //전송
			}
		);
	}
	
	/**
	 * [SSE 통신]dummy data 생성
	 * : 503 Service Unavailable 방지
	 */
	private Notification createDummyNotification(String receiver) {
		return Notification.builder()
				.notificationId(receiver + "_" + System.currentTimeMillis())
				.receiver(receiver)
				.content("send dummy data to client.")
				.notificationType(NotificationType.NOTICE.getAlias())
				.url(NotificationType.NOTICE.getPath())
				.readYn('N')
				.deletedYn('N')
				.build();
	}
	
	/**
	 * [SSE 통신]notification type별 data 생성
	 */
	private Notification createNotification(String receiver, String content, String type, String url) {
		if(type.equals("comment")) { //댓글
			return Notification.builder()
					.receiver(receiver)
					.content(content)
					.notificationType(NotificationType.COMMENT.getAlias())
					.url(url)
					.readYn('N')
					.deletedYn('N')
					.build();
		} else if(type.equals("nestedComment")) { //대댓글
			return Notification.builder()
					.receiver(receiver)
					.content(content)
					.notificationType(NotificationType.REPLY.getAlias())
					.url(url)
					.readYn('N')
					.deletedYn('N')
					.build();
		} else {
			return null;
		}
	}
	
	/**
	 * [SSE 통신]notification type별 event 전송
	 */
	private void send(SseEmitter sseEmitter, String emitterId, Object data) {
		try {
			sseEmitter.send(SseEmitter.event()
					.id(emitterId)
					.name("sse")
					.data(data, MediaType.APPLICATION_JSON));
		} catch(IOException exception) {
			sseRepository.deleteEmitterById(emitterId);
			sseEmitter.completeWithError(exception);
		}
	}
	
	/**
	 * [SSE 통신]
	 */
	private void emitEventToClient(SseEmitter sseEmitter, String emitterId, Object data) {
		try {
			send(sseEmitter, emitterId, data);
			sseRepository.deleteEmitterById(emitterId);
		} catch (Exception e) {
			sseRepository.deleteEmitterById(emitterId);
			throw new RuntimeException("Connection Failed.");
		}
	}
}
