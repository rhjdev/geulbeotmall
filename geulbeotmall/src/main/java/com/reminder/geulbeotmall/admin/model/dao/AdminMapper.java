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

	int getClosedNumber();
	
	List<MemberDTO> getMemberOnly();

	List<MemberDTO> getAdminOnly();

	List<MemberDTO> getClosedOnly();
	
	int updateAuth();

	int searchAuthById(String memberId);

	int deleteAuthAsAdmin(String memberId);

	int insertAuthAsAdmin(String memberId);
	
	int updateAccSuspension(String memberId);
	
	int insertAccSuspension(String memberId, String accSuspDesc);

	int updateAccActivation(String memberId);
}
