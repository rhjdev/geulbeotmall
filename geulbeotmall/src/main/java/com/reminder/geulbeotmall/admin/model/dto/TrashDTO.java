package com.reminder.geulbeotmall.admin.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TrashDTO {

	private int trashNo;
	private String refBoard;
	private int refPostNo;
	private String trashTitle;
	private String trashWriter;
	private String trashDeleteBy;
	private Date trashDateMoved;
	private Date trashDateUntil;
}
