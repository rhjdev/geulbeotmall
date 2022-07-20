package com.reminder.geulbeotmall.product.model.dto;

public enum ProductTag {
	
	A("153시리즈"),
	B("국제표준규격호환가능"),
	C("깔끔한필기감"),
	D("다양한바디컬러"),
	E("데코레이션용"),
	F("드로잉"),
	G("만년필"),
	H("밝고선명한색상"),
	I("부드러운필기감"),
	J("샤프/샤프심"),
	K("선물용"),
	L("세밀한필기용"),
	M("세트상품"),
	N("수성마카"),
	O("수성펜"),
	P("시그니처제품"),
	Q("유성마카"),
	R("유성펜"),
	S("중간굵기필기용"),
	T("진하고선명한색상"),
	U("카트리지형리필방식"),
	V("컨버터리필방식"),
	W("컬러링용"),
	X("트윈타입"),
	Y("프리미엄펜");
	
	private final String label;
	
	ProductTag(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
