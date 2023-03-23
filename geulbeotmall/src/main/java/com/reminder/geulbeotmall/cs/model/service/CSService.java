package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;

public interface CSService {

	int writeAInquiry(InquiryDTO inquiryDTO);

	List<InquiryDTO> getMyInquiryList(String memberId);
}
