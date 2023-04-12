package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface CSService {

	int writeAInquiry(InquiryDTO inquiryDTO);
	
	int getTotalInquiryNumber(String memberId, Criteria criteria, String type);

	List<InquiryDTO> getMyInquiryList(String memberId, Criteria criteria, String type);

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);

	int updateInquiryAnsweredYn(int refPostNo);
}
