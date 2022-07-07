package com.reminder.geulbeotmall.product.model.dto;

/* enum(enumeration type) : 열거체 */
public enum DiscountRate {
	
	ZERO(0),
	TEN(10),
	THIRTY(30);
	
	/* DiscountRate.ZERO 형식으로 쓰이도록 선언 */
	
	private final int value;
	
	DiscountRate(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
