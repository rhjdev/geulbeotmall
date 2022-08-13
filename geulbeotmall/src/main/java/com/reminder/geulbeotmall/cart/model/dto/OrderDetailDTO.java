package com.reminder.geulbeotmall.cart.model.dto;

import java.util.Date;
import lombok.Data;

@Data
public class OrderDetailDTO {

	private int orderNo;
	private String memberId;
	private Date orderDate;
	private String rcvrName;
	private String rcvrPhone;
	private String rcvrAddress;
	private String dlvrReqMessage;
	private String dlvrStatus;
}
