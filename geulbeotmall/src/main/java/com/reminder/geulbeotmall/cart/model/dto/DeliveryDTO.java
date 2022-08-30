package com.reminder.geulbeotmall.cart.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DeliveryDTO {

	private String orderNo;
	private int deliveryFee;
	private String deliveryCompany;
	private Date dispatchDate;
	private Date deliveryDate;
}
