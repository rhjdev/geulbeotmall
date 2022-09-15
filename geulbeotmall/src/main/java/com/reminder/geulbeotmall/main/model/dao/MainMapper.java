package com.reminder.geulbeotmall.main.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.product.model.dto.ProductDTO;

@Mapper
public interface MainMapper {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();
}
