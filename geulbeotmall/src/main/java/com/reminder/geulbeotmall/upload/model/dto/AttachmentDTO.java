package com.reminder.geulbeotmall.upload.model.dto;

import lombok.Data;

@Data
public class AttachmentDTO {
	
	private int attachmentNo;		//첨부파일번호
	private int refProdNo;		    //상품번호
	private int refRevwNo;			//리뷰번호
	private String origFileName;	//원본파일명
	private String saveFileName;	//저장파일명
	private String savePath;		//저장경로
	private String thumbnailPath;	//썸네일경로
	private String fileType; 		//파일유형(THUMB or CONTENT)
	private char attachStatusYn;	//게재여부
}
