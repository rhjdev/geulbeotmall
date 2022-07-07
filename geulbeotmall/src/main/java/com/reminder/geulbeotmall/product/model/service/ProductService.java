package com.reminder.geulbeotmall.product.model.service;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;

public interface ProductService {

	List<CategoryDTO> getCategoryList();

	int checkCategoryName(String categoryName);
	
	int addANewCategory(String categoryName);

	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);

}
