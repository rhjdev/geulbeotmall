package com.reminder.geulbeotmall.community.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriUtils;

import com.reminder.geulbeotmall.community.model.service.DownloadService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DownloadController {
	
	private final DownloadService downloadService;
	
	@Autowired
	public DownloadController(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	@GetMapping("/download/{no}")
	public void downloadRequest(@PathVariable("no") int attachmentNo, HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("attachment download request no : {}", attachmentNo);
		
		AttachmentDTO attachmentDTO = downloadService.getAttachmentFile(attachmentNo);
		
		String realPath = request.getSession().getServletContext().getRealPath("/");
		log.info("src/main/webapp : {}", realPath);
		
		File target = new File(realPath, attachmentDTO.getSavePath());
		response.setContentType("application/download");
		response.setContentLength((int) target.length());
		response.setHeader("Content-disposition", "attachment;filename=\"" + attachmentDTO.getSaveFileName() + "\""); //저장명
		OutputStream os = response.getOutputStream();
		
		FileInputStream fis = new FileInputStream(target);
		FileCopyUtils.copy(fis, os);
		
		int result = downloadService.incrementDownloadCount(attachmentNo);
		if(result == 1) {
			fis.close();
			os.close();
		}
	}
}
