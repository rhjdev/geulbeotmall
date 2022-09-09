package com.reminder.geulbeotmall.product.model.service;

import java.util.List;

import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface ProductService {

	List<CategoryDTO> getCategoryList();
	
	int checkCategoryNo(String categoryName);

	int checkCategoryName(String categoryName);
	
	int addANewCategory(String categoryName, String categorySection);

	List<BrandDTO> getBrandList();
	
	int checkBrandNo(String brandName);
	
	int checkBrandName(String brandName);
	
	int addANewBrand(String brandName);
	
	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);

	int addProduct(int categoryNo, String prodName, String prodDesc, String productTag, int discountRate,
			int prodPrice, int brandNo, String prodOrigin, String prodDetailContent);

	int attachProdThumbnail(AttachmentDTO attachment);
	
	int attachProdContentImage(AttachmentDTO attachment);
	
	int checkCurrProdNo();

	ProductDTO getProductDetails(int prodNo);

	int getTotalNumber(Criteria criteria);
	
	int getOnSaleNumber(Criteria criteria);
	
	int getStopSaleNumber(Criteria criteria);
	
	int getOnDiscountNumber(Criteria criteria);
	
	int getSoldOutNumber(Criteria criteria);

	List<ProductDTO> getProductList(Criteria criteria);
	
	List<ProductDTO> getOnSaleOnly(Criteria criteria);
	
	List<ProductDTO> getStopSaleOnly(Criteria criteria);
	
	List<ProductDTO> getOnDiscountOnly(Criteria criteria);
	
	List<ProductDTO> getSoldOutOnly(Criteria criteria);

	AttachmentDTO getMainThumbnailByProdNo(int prodNo);
	
	AttachmentDTO getSubThumbnailByProdNo(int prodNo);

	List<OptionDTO> getOptionListByProdNo(int prodNo);

	String searchSaleYnByProdNo(int prodNo);

	int stopSellingAProduct(int prodNo);

	int putAProductOnSale(int prodNo);

	int deleteProduct(int prodNo);

	int searchProdNoByOptionNo(int optionNo);

	List<ProductDTO> getProductListByCategorySection(String category);

	List<String> getCategorySection();

	List<CategoryDTO> getCategoryListBySection(String category);

	int getTotalNumberBySection(String category);

	Integer getSalesByProdNo(int prodNo);

	CategoryDTO getCategoryByProdNo(int prodNo);

	BrandDTO getBrandByProdNo(int prodNo);

	List<ReviewDTO> getReviewListByProdNo(int prodNo);

	double averageReviewRating(int prodNo);

	int getPercentageOfRating(int total, int prodNo, int star);

	int getNumberOfRatings(int prodNo, int star);

	int getTotalNumberOfReviews(int prodNo);
}
