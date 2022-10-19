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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2Service {
	
	@Value("${custom.kakao.apiKey}")
	private String K_CLIENT_ID;
	@Value("${custom.kakao.redirectUri}")
	private String K_REDIRECT_URI;
	
	/**
	 * 카카오 로그인1. 인가 코드 받기
	 */
	public String getKakaoSignInUrl() {
		StringBuffer url = new StringBuffer("https://kauth.kakao.com/oauth/authorize?response_type=code");
		url.append("&client_id=").append(K_CLIENT_ID);
		url.append("&redirect_uri=").append(K_REDIRECT_URI);
		return url.toString();
	}
	
	/**
	 * 카카오 로그인2. 토큰 받기
	 */
	public String getKakaoAccessToken(String code) {
		log.info("kakao authorization code 통해 access token 요청");
		String access_token = "";
		String refresh_token = "";
		try {
			URL url = new URL("https://kauth.kakao.com/oauth/token");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true); //POST 요청 위해 기본값이 false인 dooutput을 true로 명시
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=").append(K_CLIENT_ID);
			sb.append("&redirect_uri=").append(K_REDIRECT_URI);
			sb.append("&code=").append(code);
			bw.write(sb.toString());
			bw.flush();
			
			int responseCode = conn.getResponseCode(); //200인 경우 succeed
			log.info("getKakaoAccessToken connection response code : {}", responseCode);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String message = "";
			String result = "";
			while((message = br.readLine()) != null) {
				result += message;
			}
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			access_token = element.getAsJsonObject().get("access_token").getAsString();
			refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();
			log.info("access_token : {}", access_token);
			log.info("refresh_token : {}", refresh_token);
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_token;
	}
	
	/**
	 * 카카오 로그인3. 유저 정보 가져오기
	 */
	public MemberDTO getKakaoUserInfo(String access_token) {
		MemberDTO member = new MemberDTO();
		try {
			URL url = new URL("https://kapi.kakao.com/v2/user/me");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + access_token); //'Bearer ' 공백 주의
			conn.setRequestProperty("charset", "UTF-8");
			
			int responseCode = conn.getResponseCode(); //200인 경우 succeed
			log.info("getKakaoUserInfo connection response code : {}", responseCode);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String message = "";
			String result = "";
			while((message = br.readLine()) != null) {
				result += message;
			}
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
			JsonObject account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
			
			String id = element.getAsJsonObject().get("id").getAsString();
			String nickname = properties.getAsJsonObject().get("nickname").getAsString();
			String email = account.getAsJsonObject().get("email").getAsString();
			log.info("kakao id : {}", id);
			log.info("kakao nickname : {}", nickname);
			log.info("kakao email : {}", email);
			member.setMemberId(email);
			member.setName(nickname);
			member.setEmail(email);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return member;
	}
}
