package com.reminder.geulbeotmall.product.model.dto;

public enum CategorySection {

	A("프리미엄펜"),
	B("펜/펜슬"),
	C("마카/컬러링"),
	D("디자인문구"),
	E("사무용품");
	
	private final String label;
	
	CategorySection(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
