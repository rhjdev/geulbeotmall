package com.reminder.geulbeotmall.cart.model.dto;

import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

	private int cartNo;
	private String memberId;
	private int prodNo;
	private int optionNo;
	private int quantity;
	private ProductDTO product;
	private OptionDTO option;
}
