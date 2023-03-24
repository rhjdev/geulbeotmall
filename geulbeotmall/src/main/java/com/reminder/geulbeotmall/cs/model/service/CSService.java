package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

public interface CSService {

	int writeAInquiry(InquiryDTO inquiryDTO);

	List<InquiryDTO> getMyInquiryList(String memberId);

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);
}
