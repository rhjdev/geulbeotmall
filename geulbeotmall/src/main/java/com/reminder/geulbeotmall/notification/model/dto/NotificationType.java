package com.reminder.geulbeotmall.notification.model.dto;

public enum NotificationType {
	
	COMMENT("/cs/inquiry/details?no=", "comment"),
	REPLY("/cs/inquiry/details?no=", "nestedComment"),
	DISPATCH("/mypage/order/details?no=", "dispatch"),
	DELIVERY("/mypage/review", "delivery"),
	NOTICE("/cs/main", "notice");
	
	private final String path;
	private final String alias;
	
	NotificationType(String path, String alias) {
		this.path = path;
		this.alias = alias;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getAlias() {
		return alias;
	}
}
