package com.reminder.geulbeotmall.review.model.service;

import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface ReviewService {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);

	int savePoints(PointDTO pointDTO);

	int incrementReviewViewCount(int reviewNo);

	String getAttachmentByReviewNo(int reviewNo, int num);
	
	ReviewDTO getReviewDetails(int reviewNo);

	int checkReviewNoToEdit(String memberId, String orderNo, int optionNo);

	int updateAReview(ReviewDTO reviewDTO);
}
