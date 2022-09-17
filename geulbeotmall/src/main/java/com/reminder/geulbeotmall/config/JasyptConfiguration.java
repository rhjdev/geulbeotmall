package com.reminder.geulbeotmall.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class JasyptConfiguration {
	
	/*
	 * 1. pom.xml 통해 jasypt 의존성 추가
	 * 2. application.yml 및 JasyptConfiguration 클래스에서 관련 config 설정
	 * 3. 암복호화를 위한 key는 'jasypt.encryptor.password'로 정의하고, 보안 측면을 고려하여 불필요한 노출 없이 실행 시에 값이 자동으로 넘어갈 수 있도록 VM arguments로 등록
	 * => (Eclipse 환경 예시) Run Configurations > Arguments > VM arguments > -Djasypt.encryptor.password=myKey
	 */
	@Autowired
	private Environment environment;
	
	public JasyptConfiguration(Environment environment) {
		this.environment = environment;
	}

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(environment.getProperty("jasypt.encryptor.password"));
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		
		encryptor.setConfig(config);
		return encryptor;
	}
}
