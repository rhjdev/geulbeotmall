package com.reminder.geulbeotmall.main.model.service;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.ProductDTO;

public interface MainService {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();
}
