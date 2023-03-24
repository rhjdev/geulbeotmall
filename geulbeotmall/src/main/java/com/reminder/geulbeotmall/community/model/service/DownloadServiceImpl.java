package com.reminder.geulbeotmall.community.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.community.model.dao.DownloadMapper;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Service("downloadService")
public class DownloadServiceImpl implements DownloadService {
	
	private final DownloadMapper downloadMapper;
	
	@Autowired
	public DownloadServiceImpl(DownloadMapper downloadMapper) {
		this.downloadMapper = downloadMapper;
	}

	@Override
	public AttachmentDTO getAttachmentFile(int attachmentNo) {
		return downloadMapper.getAttachmentFile(attachmentNo);
	}

	@Override
	public int checkCurrAttachNo() {
		return downloadMapper.checkCurrAttachNo();
	}

	@Override
	public int setDownloadCount(int currAttachNo) {
		return downloadMapper.setDownloadCount(currAttachNo);
	}
	
	@Override
	public int incrementDownloadCount(int attachmentNo) {
		return downloadMapper.incrementDownloadCount(attachmentNo);
	}
}
