package com.reminder.geulbeotmall.cart.model.service;

import java.util.List;

import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface CartService {
	
	List<CartDTO> getCurrentCart(String memberId);
	
	CartDTO getCartItemByOptionNo(String username, int optionNo);

	List<CartDTO> getCartItemList(CartDTO cartDTO);

	OptionDTO searchOptionInfoByOptionNo(int optionNo);

	ProductDTO searchProductInfoByOptionNo(int optionNo);

	BrandDTO searchBrandInfoByOptionNo(int optionNo);
}
