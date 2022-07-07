package com.reminder.geulbeotmall.product.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class StockDTO {

	private int stockNo;
	private int stockAmount;
	private Date stockDate;
}
