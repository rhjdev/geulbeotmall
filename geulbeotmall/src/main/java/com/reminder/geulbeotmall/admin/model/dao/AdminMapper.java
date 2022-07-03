package com.reminder.geulbeotmall.admin.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;

@Mapper
public interface AdminMapper {

	List<MemberDTO> getMemberList(Criteria criteria);

	MemberDTO getMemberDetails(String memberId);

	int getTotalNumber(Criteria criteria);

	int getRegularNumber(Criteria criteria);

	int getAdminNumber(Criteria criteria);

	int getClosedNumber();
	
	List<MemberDTO> getMemberOnly(Criteria criteria);

	List<MemberDTO> getAdminOnly(Criteria criteria);

	List<MemberDTO> getClosedOnly();
	
	int updateAuth();

	int searchAuthById(String memberId);

	int deleteAuthAsAdmin(String memberId);

	int insertAuthAsAdmin(String memberId);
	
	int updateAccSuspension(String memberId);
	
	int insertAccSuspension(String memberId, String accSuspDesc);

	int updateAccActivation(String memberId);
}
