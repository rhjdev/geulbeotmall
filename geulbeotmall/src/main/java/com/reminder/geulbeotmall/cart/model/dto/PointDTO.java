package com.reminder.geulbeotmall.cart.model.dto;

import lombok.Data;

@Data
public class PointDTO {

	private int pointNo;
	private String paymentNo;
	private int pointAmount;
	private String pointDateTime;
	private String pointStatus;
}
