package com.reminder.geulbeotmall.cs.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class InquiryDTO {

	private int inquiryNo;
	private String memberId;
	private String inquiryTitle;
	private String inquiryContent;
	private String inquiryType; //enum
	private Date inquiryRegDate;
	private char inquiryAnsweredYn;
}
