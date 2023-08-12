package com.reminder.geulbeotmall.member.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.config.GeulbeotmallApplication;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.RoleDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {GeulbeotmallApplication.class})
public class MemberMapperTests {
	
	/*
	 * '패스워드'와 같은 환경 변수 관리 차원에서 직접 VM arguments를 등록해 적용 중... 새로운 Tests 파일 생성 시 참고하여 추가 등록 필요
	 * (Run Configurations - Spring Boot App - Arguments)
	 */
	
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public MemberMapperTests(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
		this.memberMapper = memberMapper;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Test
	@DisplayName("매퍼 인터페이스 의존성 주입 테스트")
	public void testInit() {
		assertNotNull(memberMapper);
		assertNotNull(passwordEncoder);
		/* 1. dao 폴더 하위 MemberMapper 인터페이스에 @Mapper 등록
		 * 2. config 폴더 하위 MybatisConfiguration 클래스에 @MapperScan 적용
		 */
	}
	
	@Test
	@DisplayName("신규 회원 등록용 매퍼 성공 테스트")
	//@Disabled
	public void testSignUpMember() {
		//given
		for(int i=0; i < 100; i++) { //100개 dummy data 생성
			String randomId = "";
			String alphabet = "abcdefghijklmnopqrstuvwxyz";
			for(int j=0; j < 6; j++) {
				randomId += alphabet.charAt((int) (Math.random() * alphabet.length()));
			}
			
			String password = passwordEncoder.encode("Pass123!");
			
			List<String> familyName = Arrays.asList("김", "이", "박", "최", "정", "강", "조", "윤", "장", "임");
			List<String> hangeul = Arrays.asList("가", "강", "건", "경", "고", "관", "광", "구", "규", "근", "기", "길", "나", "남", "노", "누", "다",
			        "단", "달", "담", "대", "덕", "도", "동", "두", "라", "래", "로", "루", "리", "마", "만", "명", "무", "문", "미", "민", "바", "박",
			        "백", "범", "별", "병", "보", "빛", "사", "산", "상", "새", "서", "석", "선", "설", "섭", "성", "세", "소", "솔", "수", "숙", "순",
			        "숭", "슬", "승", "시", "신", "아", "안", "애", "엄", "여", "연", "영", "예", "오", "옥", "완", "요", "용", "우", "원", "월", "위",
			        "유", "윤", "율", "으", "은", "의", "이", "익", "인", "일", "잎", "자", "잔", "장", "재", "전", "정", "제", "조", "종", "주", "준",
			        "중", "지", "진", "찬", "창", "채", "천", "철", "초", "춘", "충", "치", "탐", "태", "택", "판", "하", "한", "해", "혁", "현", "형",
			        "혜", "호", "홍", "화", "환", "회", "효", "훈", "휘", "희", "운", "모", "배", "부", "림", "봉", "혼", "황", "량", "린", "을", "비",
			        "솜", "공", "면", "탁", "온", "디", "항", "후", "려", "균", "묵", "송", "욱", "휴", "언", "령", "섬", "들", "견", "추", "걸", "삼",
			        "열", "웅", "분", "변", "양", "출", "타", "흥", "겸", "곤", "번", "식", "란", "더", "손", "술", "훔", "반", "빈", "실", "직", "흠",
			        "흔", "악", "람", "뜸", "권", "복", "심", "헌", "엽", "학", "개", "롱", "평", "늘", "늬", "랑", "얀", "향", "울", "련");
			List<String> gu = Arrays.asList("서울 강남구", "서울 강동구", "서울 강북구", "서울 강서구", "서울 관악구", "서울 광진구", "서울 구로구", "서울 금천구",
					"서울 노원구", "서울 도봉구", "서울 동대문구", "서울 동작구", "서울 마포구", "서울 서대문구", "서울 서초구", "서울 성동구", "서울 성북구", "서울 송파구",
					"서울 양천구", "서울 영등포구", "서울 용산구", "서울 은평구", "서울 종로구", "서울 중구", "서울 중랑구",
					"경기도 고양시 덕양구", "경기도 고양시 일산동구", "경기도 고양시 일산서구", "경기도 성남시 분당구", "경기도 성남시 수정구", "경기도 성남시 중원구",
					"경기도 수원시 권선구", "경기도 수원시 영통구", "경기도 수원시 장안구", "경기도 수원시 팔달구", "경기도 안양시 동안구", "경기도 안양시 만안구",
					"경기도 안산시 단원구", "경기도 안산시 상록구", "경기도 용인시 기흥구", "경기도 용인시 수지구", "경기도 용인시 처인구");
			Collections.shuffle(familyName);
			Collections.shuffle(hangeul);
			Collections.shuffle(gu);
			
			String threeDigit = new Random().nextInt(300) + "";
			String fourDigit = (int) (Math.random() * 8999) + 1000 + "";
			String fiveDigit = new Random().nextInt(10000, 99999) + "";
			
			/* 1. 회원 등록 */
			/* 2. 권한 등록 */
			/* 3. 인증필요여부 등록(기본 N) */
			/* 4. 적립금 혜택 등록 */
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			cal.set(2023, 6, 1); //month+1이므로 0~11
			//String specificDate = simpleDateFormat.format(cal.getTime());
			//String today = simpleDateFormat.format(new Date());
			
			MemberDTO member = new MemberDTO();
			member.setMemberId(randomId + fourDigit);
			member.setMemberPwd(password);
			member.setName(familyName.get(0) + hangeul.get(0) + hangeul.get(1));
			member.setPhone("010" + fourDigit + new StringBuffer(fourDigit).reverse().toString());
			member.setEmail(randomId + "@reminder.com");
			member.setAddress(fiveDigit + "$" + gu.get(0) + " " + hangeul.get(10) + hangeul.get(11) + "로 " + threeDigit + "$" 
							+ hangeul.get(12) + hangeul.get(13) + "아파트" + " 10" + fourDigit.substring(2) + "호");
			member.setAgreement('Y');
			member.setAccInactiveYn('N');
			member.setAccCreationDate(cal.getTime());
			member.setAccChangedDate(cal.getTime());
			
			RoleDTO role = new RoleDTO();
			role.setMemberId(member.getMemberId());
			role.setAuthorityCode(1);
			
			PointDTO point = new PointDTO();
			point.setBonusReason("신규회원가입[" + member.getMemberId() + "]");
			point.setPointAmount(2000);
			point.setPointDateTime(simpleDateFormat.format(cal.getTime()));
			point.setPointStatus("적립");
			
			//when
			int resultA = memberMapper.insertMember(member);
			int resultB = memberMapper.insertRole(role);
			int resultC = memberMapper.insertAuthentication(member.getMemberId());
			int resultD = memberMapper.addNewMemberBonusPoints(point);
			
			//then
			assertEquals(1, resultA);
			assertEquals(1, resultB);
			assertEquals(1, resultC);
			assertEquals(1, resultD);
		}
	}
	
	@Test
	@DisplayName("아이디 중복 확인 매퍼 테스트")
	@Disabled
	public void testIsIdDuplicated() {
		
		//given
		String memberId = "user01";
		
		//when
		int count = memberMapper.checkId(memberId);
		
		//then
		assertEquals(1, count);
	}
	
	@Test
	@DisplayName("로그인 실패 시 카운팅 증가 여부 매퍼 테스트")
	@Disabled
	public void testLoginFailed() {
		
		//given
		String username = "user01";
		String password = "1234";
		
		//when
		memberMapper.updateLoginFailedCount(username);
		int count = memberMapper.checkLoginFailedCount(username);
		
		//then
		assertEquals(1, count);
	}
}
