package com.reminder.geulbeotmall.cart.model.dto;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class OrderDetailDTO {

	private String orderNo;
	private String memberId;
	private String orderDate;
	private String rcvrName;
	private String rcvrPhone;
	private String rcvrAddress;
	private String dlvrReqMessage;
	private String dlvrStatus;
	private OrderDTO order;
	private PaymentDTO payment;
	private OptionDTO option;
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
