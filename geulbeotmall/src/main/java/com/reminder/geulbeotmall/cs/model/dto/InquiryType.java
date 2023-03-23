package com.reminder.geulbeotmall.cs.model.dto;

public enum InquiryType {

	INFO("회원정보"),
	PRODUCT("상품"),
	ORDER("주문/결제"),
	DELIVERY("배송"),
	REFUND("교환/반품/취소"),
	RESERVE("적립금"),
	REPORT("신고"),
	SITE("홈페이지 이용");
	
	private final String label;
	
	InquiryType(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
