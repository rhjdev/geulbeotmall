package com.reminder.geulbeotmall.cart.model.dto;

import lombok.Data;

@Data
public class OrderDTO {

	private String orderNo;
	private int optionNo;
	private int orderQuantity;
	private int orderAmount;
}
