package com.reminder.geulbeotmall.admin.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TrashDTO {

	private int trashNo;
	private int refRevwNo;
	private String trashTitle;
	private String trashWriter;
	private String trashDeleteBy;
	private Date trashDateMoved;
	private Date trashDateUntil;
}
