package com.reminder.geulbeotmall.product.model.dto;

public enum PointSize {
	
	FIRST(0.3),
	SECOND(0.4),
	THIRD(0.5),
	FOURTH(0.7),
	FIFTH(0.9),
	SIXTH(1.0),
	SEVENTH(1.2),
	EIGHTH(1.5),
	NINTH(1.7),
	TENTH(2.0);
	
	private final double value;
	
	PointSize(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
}
