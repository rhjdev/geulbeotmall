package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.admin.model.dao.AdminMapper;
import com.reminder.geulbeotmall.admin.model.dto.MemberSuspDTO;
import com.reminder.geulbeotmall.admin.model.dto.SuspDTO;
import com.reminder.geulbeotmall.admin.model.dto.TrashDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.community.model.dto.CommentDTO;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
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
	public int getClosedNumber(Criteria criteria) {
		int closed = adminMapper.getClosedNumber(criteria);
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
	public List<MemberSuspDTO> getClosedOnly(Criteria criteria) {
		List<MemberSuspDTO> closedOnly = adminMapper.getClosedOnly(criteria);
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
	public int getTotalInquiryNumber(Criteria criteria) {
		return adminMapper.getTotalInquiryNumber(criteria);
	}
	
	@Override
	public int getTotalReviewNumber(Criteria criteria) {
		return adminMapper.getTotalReviewNumber(criteria);
	}
	
	@Override
	public int getTotalTrashNumber(Criteria criteria, String checkTrashRefBoard) {
		return adminMapper.getTotalTrashNumber(criteria, checkTrashRefBoard);
	}

	@Override
	public List<InquiryDTO> getTotalInquiryPostList(Criteria criteria) {
		return adminMapper.getTotalInquiryPostList(criteria);
	}
	
	@Override
	public List<ReviewDTO> getTotalReviewPostList(Criteria criteria) {
		return adminMapper.getTotalReviewPostList(criteria);
	}
	
	@Override
	public List<Map<TrashDTO, String>> getTotalTrashList(Criteria criteria, String checkTrashRefBoard) {
		return adminMapper.getTotalTrashList(criteria, checkTrashRefBoard);
	}

	@Override
	public int moveAPostToTrash(TrashDTO trashDTO) {
		return adminMapper.moveAPostToTrash(trashDTO);
	}

	@Override
	public int restoreAPostFromTrash(int trashNo) {
		/* 댓글의 경우 삭제일자 초기화 또한 적용 */
		TrashDTO trashDTO = adminMapper.getRefBoardNameFromTrash(trashNo);
		if(trashDTO.getRefBoard().equals("comment")) {
			int commentNo = trashDTO.getRefPostNo();
			adminMapper.updateCommentDelDateDefault(commentNo);
		}
		return adminMapper.restoreAPostFromTrash(trashNo);
	}

	@Override
	public List<Integer> getTrashItemToDelete() {
		return adminMapper.getTrashItemToDelete();
	}

	@Override
	public int permanentlyDeleteFromTrashAndReviewData(int reviewNo) {
		int resultA = adminMapper.permanentlyDeleteFromTrash(reviewNo);
		int resultB = adminMapper.permanentlyDeleteReviewPost(reviewNo);
		return resultA == 1 && resultB == 1 ? 1 : 0;
	}

	@Override
	public int getTotalOrderNumber(Criteria criteria) {
		return adminMapper.getTotalOrderNumber(criteria);
	}
	
	@Override
	public int getPreparingOrderNumber(Criteria criteria) {
		return adminMapper.getPreparingOrderNumber(criteria);
	}
	
	@Override
	public int getDeliveringOrderNumber(Criteria criteria) {
		return adminMapper.getDeliveringOrderNumber(criteria);
	}
	
	@Override
	public int getCompletedOrderNumber(Criteria criteria) {
		return adminMapper.getCompletedOrderNumber(criteria);
	}
	
	@Override
	public int getTotalCommentNumber(Criteria criteria) {
		return adminMapper.getTotalCommentNumber(criteria);
	}
	
	@Override
	public List<Map<ProductDTO, Integer>> getTopSalesProduct(String range, String start, String end) {
		return adminMapper.getTopSalesProduct(range, start, end);
	}
	
	@Override
	public List<Map<CommentDTO, String>> getTotalCommentList(Criteria criteria) {
		return adminMapper.getTotalCommentList(criteria);
	}

	@Override
	public List<Map<String, Integer>> getMemberDataByDate(String range, String start, String end) {
		return adminMapper.getMemberDataByDate(range, start, end);
	}

	@Override
	public List<Map<String, Integer>> getSalesDataByDate(String range, String start, String end) {
		return adminMapper.getSalesDataByDate(range, start, end);
	}
}
