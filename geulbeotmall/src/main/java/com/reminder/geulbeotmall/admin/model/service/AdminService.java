package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

public interface AdminService {

	List<MemberDTO> getMemberList();

	MemberDTO getMemberDetails(String memberId);

	int getTotalNumber();

	int getRegularNumber();

	int getAdminNumber();

}
