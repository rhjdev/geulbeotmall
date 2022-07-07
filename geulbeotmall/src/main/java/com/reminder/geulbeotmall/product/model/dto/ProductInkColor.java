package com.reminder.geulbeotmall.product.model.dto;

public enum ProductInkColor {
	
	BLACK("black", "블랙");
	
	private final String value;
	private final String label;
	
	ProductInkColor(String value, String label) {
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
