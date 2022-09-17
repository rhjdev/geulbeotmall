package com.reminder.geulbeotmall.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class JasyptApplicationTests {
	
	@Autowired
	private Environment environment;
	
	@Test
	@DisplayName("Jasypt 암복호화 테스트")
	public void testJasyptEncryptToDecrypt() {
		String plainText = ""; //입력 후 console에서 encrypted result 확인
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(environment.getProperty("jasypt.encryptor.password"));
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		
		String encrypted = encryptor.encrypt(plainText);
		String decrypted = encryptor.decrypt(encrypted);
		log.info("jasypt encrypted text : {}", encrypted);
		
		Assertions.assertThat(plainText).isEqualTo(decrypted);
	}
}
