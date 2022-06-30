package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.geulbeotmall.admin.model.dao.AdminMapper;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

@Service("adminService")
public class AdminServiceImpl implements AdminService {
	
	private AdminMapper adminMapper;
	
	@Autowired
	public AdminServiceImpl(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	@Override
	public List<MemberDTO> getMemberList() {
		List<MemberDTO> memberList = adminMapper.getMemberList();
		return memberList;
	}

	@Override
	public MemberDTO getMemberDetails(String memberId) {
		MemberDTO member = adminMapper.getMemberDetails(memberId);
		return member;
	}

	@Override
	public int getTotalNumber() {
		int total = adminMapper.getTotalNumber();
		return total;
	}

	@Override
	public int getRegularNumber() {
		int regular = adminMapper.getRegularNumber();
		return regular;
	}

	@Override
	public int getAdminNumber() {
		int admin = adminMapper.getAdminNumber();
		return admin;
	}
}
