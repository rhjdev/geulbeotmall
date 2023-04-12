package com.reminder.geulbeotmall.cs.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface CSMapper {

	int writeAInquiry(InquiryDTO inquiryDTO);
	
	int getTotalInquiryNumber(String memberId, @Param("criteria") Criteria criteria, String type);

	List<InquiryDTO> getMyInquiryList(String memberId, @Param("criteria") Criteria criteria, String type); //@Param 적용 후 mapper에서 parameterType="map" 정의 필요

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);

	int updateInquiryAnsweredYn(int refPostNo);
}
