package com.reminder.geulbeotmall.product.model.dto;

public enum PointSize {
	
	FIRST(0.5),
	SECOND(0.7),
	THIRD(1.0),
	FOURTH(1.2);
	
	private final double value;
	
	PointSize(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
}
