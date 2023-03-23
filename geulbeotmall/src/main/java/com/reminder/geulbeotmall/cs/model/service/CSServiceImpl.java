package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cs.model.dao.CSMapper;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;

@Service("csServiceImpl")
public class CSServiceImpl implements CSService {

	private CSMapper csMapper;
	
	@Autowired
	public CSServiceImpl(CSMapper csMapper) {
		this.csMapper = csMapper;
	}

	@Override
	public int writeAInquiry(InquiryDTO inquiryDTO) {
		return csMapper.writeAInquiry(inquiryDTO);
	}

	@Override
	public List<InquiryDTO> getMyInquiryList(String memberId) {
		return csMapper.getMyInquiryList(memberId);
	}
}
