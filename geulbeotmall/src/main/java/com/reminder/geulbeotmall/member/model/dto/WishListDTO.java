package com.reminder.geulbeotmall.member.model.dto;

import java.util.List;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class WishListDTO {

	private String memberId;
	private int optionNo;
	private ProductDTO product;
	private OptionDTO option;
	private CategoryDTO category;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
