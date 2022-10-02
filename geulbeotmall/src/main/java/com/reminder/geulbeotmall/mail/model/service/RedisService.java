package com.reminder.geulbeotmall.mail.model.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	/*
	 * Redis
	 * : 유효기간이 한정된 데이터를 key-value 한 쌍 형태로 관리하는 데이터 스토리지
	 *   1)데이터에 휘발성이 있어 별도의 만료 처리가 요구되지 않고, 2)빠른 액세스 속도가 보장된다는 특징에서 Refresh Token을 다루기에 적합하다.
	 *   
	 *   => Refresh Token 사용 목적은 기존 Access Token의 유효기간을 단축하는 대신 재발급 횟수를 늘려 보안을 강화하되 사용자가 체감하는 로그아웃 빈도에는 이상이 없도록 하는 데에 있다.
	 * 
	 * Lettuce
	 * : Redis Client 구현체는 크게 Lettuce와 Jedis로 나뉘는데, 그 중에서도 Lettuce는 spring-boot-starter-data-redis 통해 별도의 설정 없이 사용할 수가 있다.
	 *   또, Netty(비동기 이벤트 기반 고성능 네트워크 프레임워크)에 기반을 두고 비동기로 요청을 처리하기 때문에 빠른 액세스 속도를 보장한다.
	 */
	
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public String getKey(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(key);
	}
	
	public void setKeyAndValue(String key, String value) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(key, value);
	}
	
	public void setTimeout(String key, String value, long lifetime) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		Duration expirationDuration = Duration.ofDays(lifetime);
		valueOperations.set(key, value, expirationDuration);
	}
	
	public void deleteKey(String key) {
		redisTemplate.delete(key);
	}
}
