package com.reminder.geulbeotmall.review.model.dto;

import java.util.Date;
import java.util.List;

import com.reminder.geulbeotmall.cart.model.dto.DeliveryDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class ReviewDTO {

	private int reviewNo;
	private int optionNo;
	private String orderNo;
	private String memberId;
	private String revwTitle;
	private String revwContent;
	private Date revwRegDate;
	private int revwHits;
	private int revwRatings;
	private OptionDTO option;
	private ProductDTO product;
	private OrderDTO order;
	private OrderDetailDTO orderDetail;
	private DeliveryDTO delivery;
	private List<AttachmentDTO> attachmentList;
}
