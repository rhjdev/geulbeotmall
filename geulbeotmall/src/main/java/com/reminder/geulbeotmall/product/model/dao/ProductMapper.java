package com.reminder.geulbeotmall.product.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Mapper
public interface ProductMapper {

	List<CategoryDTO> getCategoryList();
	
	int checkCategoryNo(String categoryName);

	int checkCategoryName(String categoryName);

	int addANewCategory(String categoryName);

	List<BrandDTO> getBrandList();
	
	int checkBrandNo(String brandName);
	
	int checkBrandName(String brandName);
	
	int addNewBrand(String brandName);
	
	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);

	int addProduct(int categoryNo, String prodName, String prodDesc, String productTag, int discountRate,
			int prodPrice, int brandNo, String prodOrigin, String prodDetailContent);

	int attachProdThumbnail(AttachmentDTO attachment);

	int attachProdContentImage(AttachmentDTO attachment);
	
	int checkCurrProdNo();
	
	int checkNextProdNo();

	void updateProdNoContentImage();

	int incrementProdDetailViewCount(int prodNo);

	ProductDTO getProductDetails(int prodNo);

	int getTotalNumber(Criteria criteria);

	List<ProductDTO> getProductList(Criteria criteria);

	AttachmentDTO getMainThumbnailByProdNo(int prodNo);

	List<OptionDTO> getOptionListByProdNo(int prodNo);
}
