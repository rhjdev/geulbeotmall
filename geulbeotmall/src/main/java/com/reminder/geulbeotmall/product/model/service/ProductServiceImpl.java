package com.reminder.geulbeotmall.product.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;

@Service("productService")
public class ProductServiceImpl implements ProductService {
	
	private ProductMapper productMapper;
	
	@Autowired
	public ProductServiceImpl(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}
	
	@Override
	public List<CategoryDTO> getCategoryList() {
		return productMapper.getCategoryList();
	}

	@Override
	public int checkCategoryName(String categoryName) {
		return productMapper.checkCategoryName(categoryName);
	}
	
	@Override
	public int addANewCategory(String categoryName) {
		return productMapper.addANewCategory(categoryName);
	}
	
	@Override
	public List<BrandDTO> getBrandList() {
		return productMapper.getBrandList();
	}
	
	@Override
	public int checkBrandName(String brandName) {
		return productMapper.checkBrandName(brandName);
	}
	
	@Override
	public int addANewBrand(String brandName) {
		return productMapper.addNewBrand(brandName);
	}

	@Override
	public int addProductStock(int stockAmount) {
		return productMapper.addProductStock(stockAmount);
	}

	@Override
	public int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge) {
		return productMapper.addProductOption(bodyColor, inkColor, pointSize, extraCharge);
	}
}
