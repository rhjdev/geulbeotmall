package com.reminder.geulbeotmall.cart.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;

@Service("cartService")
public class CartServiceImpl implements CartService {

	private CartMapper cartMapper;
	
	@Autowired
	public CartServiceImpl(CartMapper cartMapper) {
		this.cartMapper = cartMapper;
	}
	
	@Override
	public List<CartDTO> getCurrentCart(String memberId) {
		return cartMapper.getCurrentCart(memberId);
	}
	
	@Override
	public CartDTO getCartItemByOptionNo(String username, int optionNo) {
		return cartMapper.getCartItemByOptionNo(username, optionNo);
	}

	@Override
	public List<CartDTO> getCartItemList(CartDTO cartDTO) {
		return cartMapper.getCartItemList(cartDTO);
	}

	@Override
	public OptionDTO searchOptionInfoByOptionNo(int optionNo) {
		return cartMapper.searchOptionInfoByOptionNo(optionNo);
	}

	@Override
	public ProductDTO searchProductInfoByOptionNo(int optionNo) {
		return cartMapper.searchProductInfoByOptionNo(optionNo);
	}

	@Override
	public BrandDTO searchBrandInfoByOptionNo(int optionNo) {
		return cartMapper.searchBrandInfoByOptionNo(optionNo);
	}
}
