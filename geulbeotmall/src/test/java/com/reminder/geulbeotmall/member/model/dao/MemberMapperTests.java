package com.reminder.geulbeotmall.member.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.reminder.geulbeotmall.config.GeulbeotmallApplication;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.RoleDTO;

@SpringBootTest
@ContextConfiguration(classes = {GeulbeotmallApplication.class})
public class MemberMapperTests {
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Test
	@DisplayName("매퍼 인터페이스 의존성 주입 테스트")
	public void testInit() {
		assertNotNull(memberMapper);
		/* 1. dao 폴더 하위 MemberMapper 인터페이스에 @Mapper 등록
		 * 2. config 폴더 하위 MybatisConfiguration 클래스에 @MapperScan 적용
		 */
	}
	
	@Test
	@DisplayName("신규 회원 등록용 매퍼 성공 테스트")
	public void testSignUpMember() {
		
		//given
		MemberDTO member = new MemberDTO();
		member.setMemberId("user01");
		member.setMemberPwd("pass01");
		member.setName("김회원");
		member.setPhone("01000010001");
		member.setEmail("test@reminder.com");
		member.setAddress("서울특별시");
		member.setAgreement('Y');
		
		RoleDTO role = new RoleDTO();
		role.setMemberId(member.getMemberId());
		role.setAuthorityCode(1);
		
		//when
		int resultA = memberMapper.insertMember(member);
		int resultB = memberMapper.insertRole(role);
		
		//then
		assertEquals(1, resultA);
		assertEquals(1, resultB);
	}
	
}
