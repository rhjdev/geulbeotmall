package com.reminder.geulbeotmall.cart.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.cart.model.dto.CartDTO;

@Service("cartService")
public class CartServiceImpl implements CartService {

	private CartMapper cartMapper;
	
	@Autowired
	public CartServiceImpl(CartMapper cartMapper) {
		this.cartMapper = cartMapper;
	}

	@Override
	public List<CartDTO> getCartItemList(CartDTO cartDTO) {
		return cartMapper.getCartItemList(cartDTO);
	}
}
