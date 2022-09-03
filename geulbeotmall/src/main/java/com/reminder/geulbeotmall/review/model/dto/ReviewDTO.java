package com.reminder.geulbeotmall.review.model.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ReviewDTO {

	private int reviewNo;
	private int prodNo;
	private String orderNo;
	private String memberId;
	private String revwTitle;
	private String revwContent;
	private Date revwRegDate;
	private int revwHits;
	private int revwRatings;
}
