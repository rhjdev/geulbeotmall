package com.reminder.geulbeotmall.upload.model.dto;

import lombok.Data;

@Data
public class DesignImageDTO {

	private int designImageNo;
	private int refProdNo;
	private String origImageName;
	private String saveImageName;
	private String savePath;
	private String imageType;
	private char displayStatusYn;
}
