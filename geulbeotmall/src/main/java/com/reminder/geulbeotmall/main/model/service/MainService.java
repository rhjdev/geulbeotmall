package com.reminder.geulbeotmall.main.model.service;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.DesignImageDTO;

public interface MainService {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();

	List<DesignImageDTO> getSliderImages();

	List<DesignImageDTO> getBannerImage();
}
