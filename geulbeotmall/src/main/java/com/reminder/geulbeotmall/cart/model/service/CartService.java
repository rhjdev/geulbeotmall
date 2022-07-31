package com.reminder.geulbeotmall.cart.model.service;

import java.util.List;

import com.reminder.geulbeotmall.cart.model.dto.CartDTO;

public interface CartService {

	List<CartDTO> getCartItemList(CartDTO cartDTO);

}
