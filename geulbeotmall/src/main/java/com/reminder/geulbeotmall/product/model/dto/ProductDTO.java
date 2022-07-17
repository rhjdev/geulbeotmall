package com.reminder.geulbeotmall.product.model.dto;

import java.util.Date;
import java.util.List;

import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class ProductDTO {

	private int prodNo;				  //상품번호
	private int categoryNo;			  //카테고리번호
	private int brandNo;			  //브랜드번호
	private String prodName;		  //상품명
	private String prodDesc;		  //상품한줄설명
	private String productTag;		  //주요특징태그
	private int discountRate;		  //할인율
	private int prodPrice;			  //상품원가
	private String prodOrigin;		  //원산지
	private String prodDetailContent; //상품상세내용
	private int prodDetailViewCount;  //상품상세조회수
	private Date prodEnrollDate;	  //상품등록일자
	private Date prodChangeDate;	  //상품수정일자
	private char prodAvailYn;		  //상품판매여부
	private List<AttachmentDTO> attachmentList;
	private CategoryDTO category;
	private BrandDTO brand;
}
