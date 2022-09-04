package com.reminder.geulbeotmall.review.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
