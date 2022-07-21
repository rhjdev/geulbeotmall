package com.reminder.geulbeotmall.product.model.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

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
	public int checkCategoryNo(String categoryName) {
		return productMapper.checkCategoryNo(categoryName);
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
	public int checkBrandNo(String brandName) {
		return productMapper.checkBrandNo(brandName);
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

	@Override
	public int addProduct(int categoryNo, String prodName, String prodDesc, String productTag, int discountRate,
			int prodPrice, int brandNo, String prodOrigin, String prodDetailContent) {
		int result = productMapper.addProduct(categoryNo, prodName, prodDesc, productTag, discountRate, prodPrice, brandNo, prodOrigin, prodDetailContent);
		productMapper.updateProdNoContentImage(); //상품 상세내용이미지와 해당 상품번호 연결
		return result;
	}

	@Override
	public int attachProdThumbnail(AttachmentDTO attachment) {
		return productMapper.attachProdThumbnail(attachment);
	}
	
	@Override
	public int attachProdContentImage(AttachmentDTO attachment) {
		return productMapper.attachProdContentImage(attachment);
	}

	@Override
	public int checkCurrProdNo() {
		return productMapper.checkCurrProdNo();
	}

	@Override
	public ProductDTO getProductDetails(int prodNo) {
		ProductDTO productDetail = null;
		int count = productMapper.incrementProdDetailViewCount(prodNo); //상품상세조회수 증가
		if(count > 0) {
			productDetail = productMapper.getProductDetails(prodNo); //증가 후 상세내용 조회
		}
		return productDetail;
	}

	@Override
	public int getTotalNumber(Criteria criteria) {
		return productMapper.getTotalNumber(criteria);
	}
	
	@Override
	public int getOnSaleNumber(Criteria criteria) {
		return productMapper.getOnSaleNumber(criteria);
	}
	
	@Override
	public int getStopSaleNumber(Criteria criteria) {
		return productMapper.getStopSaleNumber(criteria);
	}
	
	@Override
	public int getOnDiscountNumber(Criteria criteria) {
		return productMapper.getOnDiscountNumber(criteria);
	}
	
	@Override
	public int getSoldOutNumber(Criteria criteria) {
		return productMapper.getSoldOutNumber(criteria);
	}
	
	@Override
	public List<ProductDTO> getProductList(Criteria criteria) {
		return productMapper.getProductList(criteria);
	}
	
	@Override
	public List<ProductDTO> getOnSaleOnly(Criteria criteria) {
		return productMapper.getOnSaleOnly(criteria);
	}
	
	@Override
	public List<ProductDTO> getStopSaleOnly(Criteria criteria) {
		return productMapper.getStopSaleOnly(criteria);
	}
	
	@Override
	public List<ProductDTO> getOnDiscountOnly(Criteria criteria) {
		return productMapper.getOnDiscountOnly(criteria);
	}
	
	@Override
	public List<ProductDTO> getSoldOutOnly(Criteria criteria) {
		return productMapper.getSoldOutOnly(criteria);
	}

	@Override
	public AttachmentDTO getMainThumbnailByProdNo(int prodNo) {
		return productMapper.getMainThumbnailByProdNo(prodNo);
	}
	
	@Override
	public AttachmentDTO getSubThumbnailByProdNo(int prodNo) {
		return productMapper.getSubThumbnailByProdNo(prodNo);
	}

	@Override
	public List<OptionDTO> getOptionListByProdNo(int prodNo) {
		return productMapper.getOptionListByProdNo(prodNo);
	}

	@Override
	public String searchSaleYnByProdNo(int prodNo) {
		return productMapper.searchSaleYnByProdNo(prodNo);
	}

	@Override
	public int stopSellingAProduct(int prodNo) {
		return productMapper.stopSellingAProduct(prodNo);
	}

	@Override
	public int putAProductOnSale(int prodNo) {
		return productMapper.putAProductOnSale(prodNo);
	}

	@Override
	public int deleteProduct(int prodNo) {
		productMapper.deleteOption(prodNo);
		productMapper.deleteOptionStock();
		productMapper.deleteAttachment(prodNo);
		return productMapper.deleteProduct(prodNo);
	}
}
