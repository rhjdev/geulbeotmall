package com.reminder.geulbeotmall.product.model.dto;

public enum ProductBodyColor {
	
	PEONY("peony", "피오니"),
	VIOLA("viola", "비올라");
	
	private final String value;
	private final String label;
	
	ProductBodyColor(String value, String label) {
		this.value = value;
		this.label = label;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLabel() {
		return label;
	}
}
