package com.reminder.geulbeotmall.product.model.dto;

public enum ProductTag {
	
	A("프리미엄펜"),
	B("부드러운필기감"),
	C("깔끔한필기감"),
	D("시그니처제품"),
	E("다양한바디컬러"),
	F("진하고선명한색상"),
	G("국제표준규격호환가능"),
	H("세트상품"),
	I("수성펜"),
	J("유성펜"),
	K("샤프/샤프심"),
	L("만년필"),
	M("컨버터리필방식"),
	N("카트리지형리필방식"),
	O("세밀한필기용"),
	P("중간굵기필기용"),
	Q("선물용"),
	R("153시리즈");
	
	private final String label;
	
	ProductTag(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
