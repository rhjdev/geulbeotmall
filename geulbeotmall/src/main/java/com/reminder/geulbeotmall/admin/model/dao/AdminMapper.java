package com.reminder.geulbeotmall.admin.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

@Mapper
public interface AdminMapper {

	List<MemberDTO> getMemberList();

	MemberDTO getMemberDetails(String memberId);

	int getTotalNumber();

	int getRegularNumber();

	int getAdminNumber();

}
