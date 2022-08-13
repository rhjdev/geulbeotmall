package com.reminder.geulbeotmall.cart.model.dto;

public enum DeliveryRequest {

	A("부재 시 경비실에 맡겨주세요."),
	B("부재 시 문 앞에 놓아주세요."),
	C("부재 시 휴대폰으로 연락주세요."),
	D("문 앞에 놓아주세요.");
	
	private final String label;
	
	DeliveryRequest(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
