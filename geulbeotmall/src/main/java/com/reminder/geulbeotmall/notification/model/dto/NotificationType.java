package com.reminder.geulbeotmall.notification.model.dto;

public enum NotificationType {
	
	COMMENT("inquiry/details", "comment"),
	REPLY("inquiry/details", "nestedComment"),
	NOTICE("cs/main", "notice");
	
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
