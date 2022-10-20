package com.reminder.geulbeotmall.oauth.model.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleOAuth2Service {

	@Value("${social.google.apiKey}")
	private String G_CLIENT_ID;
	@Value("${social.google.secretKey}")
	private String G_CLIENT_SECRET;
	@Value("${social.google.redirectUri}")
	private String G_REDIRECT_URI;
	
	/* 
	 * Google Developers 문서 참고
	 * : https://developers.google.com/identity/openid-connect/openid-connect#java
	 * : https://developers.google.com/identity/protocols/oauth2/web-server
	 */
	
	/**
	 * 구글 로그인1. 인가 코드 받기
	 */
	public String getGoogleSignInUrl() {
		StringBuffer url = new StringBuffer("https://accounts.google.com/o/oauth2/v2/auth?response_type=code");
		url.append("&client_id=").append(G_CLIENT_ID);
		url.append("&redirect_uri=").append(G_REDIRECT_URI);
		url.append("&scope=").append("https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
		return url.toString();
	}
	
	/**
	 * 구글 로그인2. 토큰 받기
	 */
	public String getGoogleAccessToken(String code) {
		log.info("google authorization code 통해 access token 요청");
		String access_token = "";
		//String refresh_token = "";
		try {
			URL url = new URL("https://oauth2.googleapis.com/token");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true); //POST 요청 위해 기본값이 false인 dooutput을 true로 명시
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("code=").append(code);
			sb.append("&client_id=").append(G_CLIENT_ID);
			sb.append("&client_secret=").append(G_CLIENT_SECRET);
			sb.append("&redirect_uri=").append(G_REDIRECT_URI);
			sb.append("&grant_type=authorization_code");
			bw.write(sb.toString());
			bw.flush();
			
			int responseCode = conn.getResponseCode(); //200인 경우 succeed
			log.info("getGoogleAccessToken connection response code : {}", responseCode);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String message = "";
			String result = "";
			while((message = br.readLine()) != null) {
				result += message;
			}
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			access_token = element.getAsJsonObject().get("access_token").getAsString();
			//refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();
			log.info("access_token : {}", access_token);
			//log.info("refresh_token : {}", refresh_token);
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_token;
	}

	/**
	 * 구글 로그인3. 유저 정보 가져오기
	 */
	public MemberDTO getGoogleUserInfo(String access_token) {
		MemberDTO member = new MemberDTO();
		try {
			URL url = new URL("https://www.googleapis.com/userinfo/v2/me?access_token=");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "Bearer " + access_token); //'Bearer ' 공백 주의
			
			int responseCode = conn.getResponseCode(); //200인 경우 succeed
			log.info("getGoogleUserInfo connection response code : {}", responseCode);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String message = "";
			String result = "";
			while((message = br.readLine()) != null) {
				result += message;
			}
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			
			String id = element.getAsJsonObject().get("id").getAsString();
			String name = element.getAsJsonObject().get("name").getAsString();
			String email = element.getAsJsonObject().get("email").getAsString();
			log.info("google id : {}", id);
			log.info("google name : {}", name);
			log.info("google email : {}", email);
			member.setMemberId(id);
			member.setName(name);
			member.setEmail(email);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return member;
	}
}
