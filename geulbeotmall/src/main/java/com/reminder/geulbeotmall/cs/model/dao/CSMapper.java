package com.reminder.geulbeotmall.cs.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Mapper
public interface CSMapper {

	int writeAInquiry(InquiryDTO inquiryDTO);

	List<InquiryDTO> getMyInquiryList(String memberId);

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);
}
