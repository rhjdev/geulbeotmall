package com.reminder.geulbeotmall.notification.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.reminder.geulbeotmall.config.GeulbeotmallApplication;
import com.reminder.geulbeotmall.notification.model.dto.Notification;
import com.reminder.geulbeotmall.notification.model.dto.NotificationType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {GeulbeotmallApplication.class})
public class NotificationMapperTests {

	private final NotificationMapper notificationMapper;
	
	@Autowired
	public NotificationMapperTests(NotificationMapper notificationMapper) {
		this.notificationMapper = notificationMapper;
	}
	
	@Test
	@DisplayName("매퍼 인터페이스 의존성 주입 테스트")
	public void testInit() {
		assertNotNull(notificationMapper);
	}
	
	@Test
	@DisplayName("[알림 DB 연동]등록 성공 매퍼 테스트")
	//@Disabled
	public void testInsertNotification() {
		//given
		String username = "user04";
		String emitterId = username + "_" + System.currentTimeMillis();
		String content = "✨실시간 댓글/대댓글 알림을 받아보세요";
		String url = NotificationType.NOTICE.getPath();
		Notification notification = Notification.builder()
											.notificationId(emitterId)
											.receiver(username)
											.content(content)
											.notificationType(NotificationType.NOTICE.getAlias())
											.url(url)
											.readYn('N')
											.deletedYn('N')
											.build();
		//when
		int create = notificationMapper.insertNotification(notification);
		//then
		assertEquals(true, create == 1 ? true : false);
	}
	
	@Test
	@DisplayName("[알림 DB 연동]조회 성공 매퍼 테스트")
	public void testReadNotification() {
		//given
		String username = "user04";
		int count = 0;
		//when
		List<Notification> notificationList = notificationMapper.getAllNotificationByUsername(username);
		log.info("notificationList size : {}", notificationList.size());
		if(notificationList != null) {
			for(int i=0; i < notificationList.size(); i++) {
				if(notificationList.get(i).getNotificationId().contains(username)) {
					count++;
				}
			}
			log.info("notificationList count id :{}", count);
		}
		//then
		assertEquals(true, notificationList.size() == count ? true : false);
	}
	
	@Test
	@DisplayName("[알림 DB 연동]읽음 상태 업데이트 성공 매퍼 테스트")
	@Disabled
	public void testUpdateReadStatusOfNotification() {
		//given
		String username = "user04";
		//when
		int update = notificationMapper.updateNotificationReadStatusByUsername(username);
		//then
		assertEquals(true, update == 1 ? true : false);
	}
	
	@Test
	@DisplayName("[알림 DB 연동]삭제 상태 업데이트 성공 매퍼 테스트")
	@Disabled
	public void testUpdateDeleteStatusOfNotification() {
		//given
		String notificationId = "user04_1692693008665";
		//when
		int delete = notificationMapper.updateNotificationDeleteStatusById(notificationId);
		//then
		assertEquals(true, delete == 1 ? true : false);
	}
}
