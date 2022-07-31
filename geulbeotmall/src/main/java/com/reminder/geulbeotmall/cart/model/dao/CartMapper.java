package com.reminder.geulbeotmall.cart.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cart.model.dto.CartDTO;

@Mapper
public interface CartMapper {
	
	List<CartDTO> getMemberCart(String id);

	List<CartDTO> getCartItemList(CartDTO cartDTO);

	int checkCartItem(int optionNo);

	void addToCart(int optionNo);

	void addToCart(CartDTO cartDTO);
}
