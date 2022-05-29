package com.reminder.geulbeotmall.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.RoleDTO;

@Mapper
public interface MemberMapper {
	
	/* 회원가입 */
	int checkId(String memberId);				//아이디 중복 검사
	
	int checkEmail(String email);				//이메일 중복 검사
	
	int insertMember(MemberDTO member);			//회원 정보 등록

	int insertRole(RoleDTO role);				//회원 권한 등록

	MemberDTO findMemberById(String username);

}
