package com.reminder.geulbeotmall.review.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.review.model.dao.ReviewMapper;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Service("reviewService")
public class ReviewServiceImpl implements ReviewService {
	
	private ReviewMapper reviewMapper;
	
	@Autowired
	public ReviewServiceImpl(ReviewMapper reviewMapper) {
		this.reviewMapper = reviewMapper;
	}

	@Override
	public int checkCurrReviewNo() {
		return reviewMapper.checkCurrReviewNo();
	}

	@Override
	public int attachReviewImages(AttachmentDTO attachment) {
		return reviewMapper.attachReviewImages(attachment);
	}

	@Override
	public int postAReview(ReviewDTO reviewDTO) {
		return reviewMapper.postAReview(reviewDTO);
	}
	
	@Override
	public String getPaymentNoByOrderNo(String orderNo) {
		return reviewMapper.getPaymentNoByOrderNo(orderNo);
	}

	@Override
	public int savePoints(PointDTO pointDTO) {
		return reviewMapper.savePoints(pointDTO);
	}

	@Override
	public int incrementReviewViewCount(int reviewNo) {
		return reviewMapper.incrementReviewViewCount(reviewNo);
	}

	@Override
	public String getAttachmentByReviewNo(int reviewNo, int num) {
		return reviewMapper.getAttachmentByReviewNo(reviewNo, num);
	}
}
