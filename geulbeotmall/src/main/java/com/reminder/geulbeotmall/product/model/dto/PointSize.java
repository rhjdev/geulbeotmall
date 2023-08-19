package com.reminder.geulbeotmall.product.model.dto;

public enum PointSize {
	
	FIRST(0.3, "height: 0.5px;"),
	SECOND(0.4, "height: 0.7px;"),
	THIRD(0.5, "height: 1.0px;"),
	FOURTH(0.7, "height: 1.5px;"),
	FIFTH(0.9, "height: 2.0px;"),
	SIXTH(1.0, "height: 2.2px;"),
	SEVENTH(1.2, "height: 2.5px;"),
	EIGHTH(1.5, "height: 2.7px;"),
	NINTH(1.7, "height: 3.0px;"),
	TENTH(2.0, "height: 3.2px;");
	
	private final double value;
	private final String thickness;
	
	PointSize(double value, String thickness) {
		this.value = value;
		this.thickness = thickness;
	}
	
	public double getValue() {
		return value;
	}
	
	public String getThickness() {
		return thickness;
	}
}
