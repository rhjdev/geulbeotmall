package com.reminder.geulbeotmall.cart.model.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {

	private String orderNo;
	private String memberId;
	private String orderDate;
	private String rcvrName;
	private String rcvrPhone;
	private String rcvrAddress;
	private String dlvrReqMessage;
	private String dlvrStatus;
}
