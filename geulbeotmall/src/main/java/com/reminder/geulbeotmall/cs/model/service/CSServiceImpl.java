package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cs.model.dao.CSMapper;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Service("csService")
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
	public int getTotalInquiryNumber(String memberId, Criteria criteria, String type) {
		return csMapper.getTotalInquiryNumber(memberId, criteria, type);
	}

	@Override
	public List<InquiryDTO> getMyInquiryList(String memberId, Criteria criteria, String type) {
		return csMapper.getMyInquiryList(memberId, criteria, type);
	}

	@Override
	public int checkCurrInquiryNo() {
		return csMapper.checkCurrInquiryNo();
	}

	@Override
	public int attachInquiryImages(AttachmentDTO tempFileInfo) {
		return csMapper.attachInquiryImages(tempFileInfo);
	}

	@Override
	public InquiryDTO getInquiryDetails(String memberId, int inquiryNo) {
		return csMapper.getInquiryDetails(memberId, inquiryNo);
	}

	@Override
	public int updateInquiryAnsweredYn(int refPostNo) {
		return csMapper.updateInquiryAnsweredYn(refPostNo);
	}
}
