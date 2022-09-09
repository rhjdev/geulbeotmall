package com.reminder.geulbeotmall.review.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Mapper
public interface ReviewMapper {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);

	int savePoints(PointDTO pointDTO);

	int incrementReviewViewCount(int reviewNo);

	String getAttachmentByReviewNo(int reviewNo, int num);
}
