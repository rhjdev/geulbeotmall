package com.reminder.geulbeotmall.cart.model.dto;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class OrderDTO {

	private String orderNo;
	private int optionNo;
	private int orderQuantity;
	private int orderAmount;
	private OrderDetailDTO orderDetail;
	private DeliveryDTO delivery;
	private OptionDTO option;
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
