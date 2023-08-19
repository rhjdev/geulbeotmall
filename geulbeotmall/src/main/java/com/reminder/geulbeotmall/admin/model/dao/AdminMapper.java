package com.reminder.geulbeotmall.admin.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

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

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface AdminMapper {

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
	
	int updateAuth();

	int searchAuthById(String memberId);

	int deleteAuthAsAdmin(String memberId);

	int insertAuthAsAdmin(String memberId);
	
	int updateAccSuspension(String memberId);
	
	int insertAccSuspension(String memberId, String accSuspDesc);

	int updateAccActivation(String memberId);

	List<OrderDetailDTO> getTotalOrderList(Criteria criteria);

	List<OrderDetailDTO> getPreparingOnly(Criteria criteria);

	List<OrderDetailDTO> getDeliveringOnly(Criteria criteria);

	List<OrderDetailDTO> getCompletedOnly(Criteria criteria);

	int updateDeliveryStatus(String dlvrStatus, String orderNo);

	OrderDetailDTO getOrderDetailsByOrderNo(String orderNo);

	int updateDispatchDate(String orderNo);

	int updateDeliveryDate(String orderNo);

	int addDisplayImages(DesignImageDTO designImage);
	
	int getTotalInquiryNumber(Criteria criteria);
	
	int getTotalReviewNumber(Criteria criteria);
	
	int getTotalTrashNumber(@Param("criteria") Criteria criteria, String checkTrashRefBoard);

	List<InquiryDTO> getTotalInquiryPostList(Criteria criteria);
	
	List<ReviewDTO> getTotalReviewPostList(Criteria criteria);
	
	List<Map<TrashDTO, String>> getTotalTrashList(@Param("criteria") Criteria criteria, String checkTrashRefBoard);

	int moveAPostToTrash(TrashDTO trashDTO);
	
	TrashDTO getRefBoardNameFromTrash(int trashNo);
	
	void updateCommentDelDateDefault(int commentNo);

	int restoreAPostFromTrash(int trashNo);

	List<Map<String, Integer>> getTrashItemToDelete();
	
	int permanentlyDeleteInquiryPost(int refPostNo);

	int permanentlyDeleteReviewPost(int refPostNo);

	int permanentlyDeleteFromTrash(String refBoard, int refPostNo);

	int getTotalOrderNumber(Criteria criteria);
	
	int getPreparingOrderNumber(Criteria criteria);
	
	int getDeliveringOrderNumber(Criteria criteria);
	
	int getCompletedOrderNumber(Criteria criteria);
	
	int getTotalCommentNumber(Criteria criteria);
	
	List<Map<CommentDTO, String>> getTotalCommentList(Criteria criteria);

	List<Map<String, Integer>> getMemberDataByDate(String range, String start, String end);

	List<Map<String, Integer>> getSalesDataByDate(String range, String start, String end);

	List<Map<ProductDTO, Integer>> getTopSalesProduct(String range, String start, String end);
}
