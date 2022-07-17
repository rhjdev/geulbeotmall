package com.reminder.geulbeotmall.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

	private int optionNo;
	private int refProdNo;
	private int refStockNo;
	private String bodyColor;
	private String inkColor;
	private double pointSize;
	private int extraCharge;
	private StockDTO stock;
}
