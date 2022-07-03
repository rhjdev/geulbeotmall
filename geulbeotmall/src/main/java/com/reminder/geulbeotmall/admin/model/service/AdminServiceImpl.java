package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.admin.model.dao.AdminMapper;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;

@Service("adminService")
public class AdminServiceImpl implements AdminService {
	
	private AdminMapper adminMapper;
	
	@Autowired
	public AdminServiceImpl(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	@Override
	public List<MemberDTO> getMemberList(Criteria criteria) {
		List<MemberDTO> memberList = adminMapper.getMemberList(criteria);
		return memberList;
	}

	@Override
	public MemberDTO getMemberDetails(String memberId) {
		MemberDTO member = adminMapper.getMemberDetails(memberId);
		return member;
	}
	
	/**
	 * @return totalRecordCount
	 */
	@Override
	public int getTotalNumber(Criteria criteria) {
		int total = adminMapper.getTotalNumber(criteria);
		return total;
	}

	@Override
	public int getRegularNumber(Criteria criteria) {
		int regular = adminMapper.getRegularNumber(criteria);
		return regular;
	}

	@Override
	public int getAdminNumber(Criteria criteria) {
		int admin = adminMapper.getAdminNumber(criteria);
		return admin;
	}

	@Override
	public int getClosedNumber() {
		int closed = adminMapper.getClosedNumber();
		return closed;
	}
	
	@Override
	public List<MemberDTO> getMemberOnly(Criteria criteria) {
		List<MemberDTO> memberOnly = adminMapper.getMemberOnly(criteria);
		return memberOnly;
	}

	@Override
	public List<MemberDTO> getAdminOnly(Criteria criteria) {
		List<MemberDTO> adminOnly = adminMapper.getAdminOnly(criteria);
		return adminOnly;
	}

	@Override
	public List<MemberDTO> getClosedOnly() {
		List<MemberDTO> closedOnly = adminMapper.getClosedOnly();
		return closedOnly;
	}
	
	@Override
	public int searchAuthById(String memberId) {
		int current = adminMapper.searchAuthById(memberId);
		return current;
	}

	@Override
	public int deleteAuthAsAdmin(String memberId) {
		return adminMapper.deleteAuthAsAdmin(memberId);
	}

	@Override
	public int insertAuthAsAdmin(String memberId) {
		return adminMapper.insertAuthAsAdmin(memberId);
	}

	@Override
	public int updateAccSuspension(String memberId) {
		return adminMapper.updateAccSuspension(memberId);
	}

	@Override
	public int insertAccSuspension(String memberId, String accSuspDesc) {
		return adminMapper.insertAccSuspension(memberId, accSuspDesc);
	}

	@Override
	public int updateAccActivation(String memberId) {
		return adminMapper.updateAccActivation(memberId);
	}
}
