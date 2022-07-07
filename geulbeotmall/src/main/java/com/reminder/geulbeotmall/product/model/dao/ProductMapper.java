package com.reminder.geulbeotmall.product.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;

@Mapper
public interface ProductMapper {

	List<CategoryDTO> getCategoryList();

	int checkCategoryName(String categoryName);

	int addANewCategory(String categoryName);

	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);
	
}
