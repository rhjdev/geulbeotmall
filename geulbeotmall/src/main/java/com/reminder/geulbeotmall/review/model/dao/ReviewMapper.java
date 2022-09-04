package com.reminder.geulbeotmall.review.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Mapper
public interface ReviewMapper {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
}
