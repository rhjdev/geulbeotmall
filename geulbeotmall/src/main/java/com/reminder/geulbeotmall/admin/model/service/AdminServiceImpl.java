package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.admin.model.dao.AdminMapper;
import com.reminder.geulbeotmall.admin.model.dto.MemberSuspDTO;
import com.reminder.geulbeotmall.admin.model.dto.SuspDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.DesignImageDTO;

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
	
	@Override
	public int getSuspCount(String memberId) {
		return adminMapper.getSuspCount(memberId);
	}
	
	@Override
	public List<SuspDTO> getSuspDetails(String memberId) {
		return adminMapper.getSuspDetails(memberId);
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
	public List<MemberSuspDTO> getClosedOnly() {
		List<MemberSuspDTO> closedOnly = adminMapper.getClosedOnly();
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

	@Override
	public List<OrderDetailDTO> getTotalOrderList(Criteria criteria) {
		return adminMapper.getTotalOrderList(criteria);
	}

	@Override
	public List<OrderDetailDTO> getPreparingOnly(Criteria criteria) {
		return adminMapper.getPreparingOnly(criteria);
	}

	@Override
	public List<OrderDetailDTO> getDeliveringOnly(Criteria criteria) {
		return adminMapper.getDeliveringOnly(criteria);
	}

	@Override
	public List<OrderDetailDTO> getCompletedOnly(Criteria criteria) {
		return adminMapper.getCompletedOnly(criteria);
	}

	@Override
	public boolean updateDeliveryStatus(String dlvrStatus, String orderNo) {
		boolean result = false;
		
		int count = 0;
		if(dlvrStatus.equals("배송중")) {
			int updateDispatchDate = adminMapper.updateDispatchDate(orderNo);
			count += updateDispatchDate;
		} else {
			int updateDeliveryDate = adminMapper.updateDeliveryDate(orderNo);
			count += updateDeliveryDate;
		}
		int updateDeliveryStatus = adminMapper.updateDeliveryStatus(dlvrStatus, orderNo);
		count += updateDeliveryStatus;
		
		if(count == 2) result = true;
		return result;
	}

	@Override
	public OrderDetailDTO getOrderDetailsByOrderNo(String orderNo) {
		return adminMapper.getOrderDetailsByOrderNo(orderNo);
	}

	@Override
	public int addDisplayImages(DesignImageDTO designImage) {
		return adminMapper.addDisplayImages(designImage);
	}

	@Override
	public List<ReviewDTO> getTotalReviewPostList(Criteria criteria) {
		return adminMapper.getTotalReviewPostList(criteria);
	}
}
