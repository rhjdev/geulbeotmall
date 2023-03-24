package com.reminder.geulbeotmall.community.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

@Mapper
public interface DownloadMapper {

	AttachmentDTO getAttachmentFile(int attachmentNo);

	int checkCurrAttachNo();

	int setDownloadCount(int currAttachNo);
	
	int incrementDownloadCount(int attachmentNo);
}
