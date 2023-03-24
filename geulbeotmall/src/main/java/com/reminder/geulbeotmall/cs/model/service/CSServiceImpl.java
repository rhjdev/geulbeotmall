package com.reminder.geulbeotmall.cs.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.community.model.dao.DownloadMapper;
import com.reminder.geulbeotmall.cs.model.dao.CSMapper;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Service("csServiceImpl")
public class CSServiceImpl implements CSService {

	private CSMapper csMapper;
	private DownloadMapper downloadMapper;
	
	@Autowired
	public CSServiceImpl(CSMapper csMapper, DownloadMapper downloadMapper) {
		this.csMapper = csMapper;
		this.downloadMapper = downloadMapper;
	}

	@Override
	public int writeAInquiry(InquiryDTO inquiryDTO) {
		return csMapper.writeAInquiry(inquiryDTO);
	}

	@Override
	public List<InquiryDTO> getMyInquiryList(String memberId) {
		return csMapper.getMyInquiryList(memberId);
	}

	@Override
	public int checkCurrInquiryNo() {
		return csMapper.checkCurrInquiryNo();
	}

	@Override
	public int attachInquiryImages(AttachmentDTO tempFileInfo) {
//		int currAttachNo = downloadMapper.checkCurrAttachNo();
//		downloadMapper.setDownloadCount(currAttachNo); //첨부파일 기본 다운로드횟수 세팅
		return csMapper.attachInquiryImages(tempFileInfo);
	}

	@Override
	public InquiryDTO getInquiryDetails(String memberId, int inquiryNo) {
		return csMapper.getInquiryDetails(memberId, inquiryNo);
	}
}
