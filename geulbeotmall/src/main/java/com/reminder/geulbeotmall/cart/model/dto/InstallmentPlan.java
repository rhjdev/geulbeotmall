package com.reminder.geulbeotmall.cart.model.dto;

public enum InstallmentPlan {

	HN("하나카드", "5만원 이상", "2~8개월"),
	HD("현대카드", "5만원 이상", "2~7개월"),
	KB("KB국민카드", "5만원 이상", "2~7개월"),
	SH("신한카드", "5만원 이상", "2~7개월"),
	SS("삼성카드", "5만원 이상", "2~6개월"),
	LT("롯데카드", "5만원 이상", "2~4개월"),
	BC("BC카드", "5만원 이상", "2~7개월"),
	NH("NH농협카드", "5만원 이상", "2~8개월"),
	JB("전북카드", "5만원 이상", "2~5개월"),
	GJ("광주카드", "5만원 이상", "2~7개월");
	
	private final String label;
	private final String minimumSpending;
	private final String interestFreePeriod;
	
	InstallmentPlan(String label, String minimumSpending, String interestFreePeriod) {
		this.label = label;
		this.minimumSpending = minimumSpending;
		this.interestFreePeriod = interestFreePeriod;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getMinimumSpending() {
		return minimumSpending;
	}
	
	public String getInterestFreePeriod() {
		return interestFreePeriod;
	}
}
