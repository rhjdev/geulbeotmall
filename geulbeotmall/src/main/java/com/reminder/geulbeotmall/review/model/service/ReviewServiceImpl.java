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

	@Override
	public ReviewDTO getReviewDetails(int reviewNo) {
		return reviewMapper.getReviewDetails(reviewNo);
	}

	@Override
	public int checkReviewNoToEdit(String memberId, String orderNo, int optionNo) {
		Integer reviewNo = reviewMapper.checkReviewNoToEdit(memberId, orderNo, optionNo);
		return reviewNo == null ? 0 : reviewNo;
	}

	@Override
	public int updateAReview(ReviewDTO reviewDTO) {
		/* A.기존 첨부파일 일괄 삭제 */
		char checkFiles = reviewMapper.checkAttachedFiles(reviewDTO.getReviewNo()) > 0 ? 'Y' : 'N'; //파일 유무 확인
		if(checkFiles == 'Y') reviewMapper.deleteAttachedFiles(reviewDTO.getReviewNo()); //파일 삭제
		/* B. 상세 내용 수정 */
		return reviewMapper.updateAReview(reviewDTO);
	}
}
