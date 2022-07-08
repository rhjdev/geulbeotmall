package com.reminder.geulbeotmall.product.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;

@Mapper
public interface ProductMapper {

	List<CategoryDTO> getCategoryList();

	int checkCategoryName(String categoryName);

	int addANewCategory(String categoryName);

	List<BrandDTO> getBrandList();
	
	int checkBrandName(String brandName);
	
	int addNewBrand(String brandName);
	
	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);
}
