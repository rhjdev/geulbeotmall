package com.reminder.geulbeotmall.product.model.service;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.dto.StockDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface ProductService {

	List<CategoryDTO> getCategoryList();
	
	int checkCategoryNo(String categoryName);

	int checkCategoryName(String categoryName);
	
	int addANewCategory(String categoryName);

	List<BrandDTO> getBrandList();
	
	int checkBrandNo(String brandName);
	
	int checkBrandName(String brandName);
	
	int addANewBrand(String brandName);
	
	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);

	int addProduct(int categoryNo, String prodName, String prodDesc, String productTag, int discountRate,
			int prodPrice, int brandNo, String prodOrigin, String prodDetailContent);

	int attachProdThumbnail(ProductDTO thumbnail);

	int attachProdContentImage(AttachmentDTO attachment);
}
