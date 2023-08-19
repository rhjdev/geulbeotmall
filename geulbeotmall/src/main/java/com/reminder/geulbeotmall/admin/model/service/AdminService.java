package com.reminder.geulbeotmall.admin.model.service;

import java.util.List;
import java.util.Map;

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

public interface AdminService {
	/* 회원관리 */
	List<MemberDTO> getMemberList(Criteria criteria);

	MemberDTO getMemberDetails(String memberId);
	
	List<SuspDTO> getSuspDetails(String memberId);
	
	int getSuspCount(String memberId);
	
	int getTotalNumber(Criteria criteria);

	int getRegularNumber(Criteria criteria);

	int getAdminNumber(Criteria criteria);
	
	int getClosedNumber(Criteria criteria);

	List<MemberDTO> getMemberOnly(Criteria criteria);

	List<MemberDTO> getAdminOnly(Criteria criteria);

	List<MemberSuspDTO> getClosedOnly(Criteria criteria);
	
	int searchAuthById(String memberId);

	int deleteAuthAsAdmin(String memberId);

	int insertAuthAsAdmin(String memberId);

	int updateAccSuspension(String memberId);
	
	int insertAccSuspension(String memberId, String accSuspDesc);

	int updateAccActivation(String memberId);
	
	/* 주문/배송관리 */
	int getTotalOrderNumber(Criteria criteria);
	
	int getPreparingOrderNumber(Criteria criteria);
	
	int getDeliveringOrderNumber(Criteria criteria);
	
	int getCompletedOrderNumber(Criteria criteria);
	
	List<OrderDetailDTO> getTotalOrderList(Criteria criteria);

	List<OrderDetailDTO> getPreparingOnly(Criteria criteria);

	List<OrderDetailDTO> getDeliveringOnly(Criteria criteria);

	List<OrderDetailDTO> getCompletedOnly(Criteria criteria);

	boolean updateDeliveryStatus(String dlvrStatus, String orderNo);

	OrderDetailDTO getOrderDetailsByOrderNo(String orderNo);

	/* 디자인관리 */
	int addDisplayImages(DesignImageDTO designImage);
	
	/* 게시글관리 */
	int getTotalInquiryNumber(Criteria criteria);
	
	int getTotalReviewNumber(Criteria criteria);
	
	int getTotalTrashNumber(Criteria criteria, String checkTrashRefBoard);
	
	List<InquiryDTO> getTotalInquiryPostList(Criteria criteria);

	List<ReviewDTO> getTotalReviewPostList(Criteria criteria);
	
	List<Map<TrashDTO, String>> getTotalTrashList(Criteria criteria, String checkTrashRefBoard);

	int moveAPostToTrash(TrashDTO trashDTO);

	int restoreAPostFromTrash(int trashNo);

	List<Map<String, Integer>> getTrashItemToDelete();

	int permanentlyDeleteFromTrashAndOriginalTableData(String refBoard, int refPostNo);
	
	/* 댓글관리 */
	int getTotalCommentNumber(Criteria criteria);
	
	List<Map<ProductDTO, Integer>> getTopSalesProduct(String range, String start, String end);
	
	List<Map<CommentDTO, String>> getTotalCommentList(Criteria criteria);

	/* 통계 */
	List<Map<String, Integer>> getMemberDataByDate(String range, String start, String end);

	List<Map<String, Integer>> getSalesDataByDate(String range, String start, String end);
}
