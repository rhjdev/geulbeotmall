package com.reminder.geulbeotmall.product.model.dto;

import lombok.Data;

@Data
public class OptionDTO {

	private int optionNo;
	private StockDTO stockNo;
	private String bodyColor;
	private String inkColor;
	private double pointSize;
	private int extraCharge;
}
