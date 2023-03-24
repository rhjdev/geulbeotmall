package com.reminder.geulbeotmall.cs.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.community.model.service.DownloadService;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.cs.model.service.CSService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Controller
@RequestMapping("/cs")
@SessionAttributes("loginMember")
public class CSController {
	
	//썸네일 크기
	public static final int THUMB_WIDTH_SIZE = 540;
	public static final int THUMB_HEIGHT_SIZE = 540;
	
	private final CSService csService;
	private final DownloadService downloadService;
	private final MessageSource messageSource;

	@Autowired
	public CSController(CSService csService, DownloadService downloadService, MessageSource messageSource) {
		this.csService = csService;
		this.downloadService = downloadService;
		this.messageSource = messageSource;
	}
	
	/**
	 * 고객센터 메인
	 */
	@GetMapping("main")
	public void getCSPage(HttpSession session, Model model) {
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember != null) {
			List<InquiryDTO> myInquiryList = csService.getMyInquiryList(loginMember);
			model.addAttribute("memberInquiry", myInquiryList);
		}
	}
	
	/**
	 * 1:1 문의 작성
	 */
	@GetMapping("inquiry")
	public void getInquiryWriteForm() {}
	
	@PostMapping(value="inquiry", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public String writeAInquiry(@ModelAttribute InquiryDTO inquiryDTO,
			@RequestParam(value="files", required=false) List<MultipartFile> files, HttpServletRequest request, RedirectAttributes rttr, Locale locale) {
		log.info("inquiry 작성 요청 : {}", inquiryDTO);
		int result = csService.writeAInquiry(inquiryDTO);
		if(result == 1) {
			int fileCount = 0;
			int fileUploaded = 0;
			int setDownloadCount = 0;
			
			if(!files.get(0).getOriginalFilename().equals("")) {
				log.info("inquiry 첨부파일 추가 시작");
				
				String realPath = request.getSession().getServletContext().getRealPath("/");
				log.info("src/main/webapp : {}", realPath);
				
				String originalUploadPath = realPath + "upload" + File.separator + "inquiry" + File.separator + "original";
				String thumbnailUploadPath = realPath + "upload" + File.separator + "inquiry" + File.separator + "thumbnail";
				File originalDirectory = new File(originalUploadPath);
				File thumbnailDirectory = new File(thumbnailUploadPath);
				
				if(!originalDirectory.exists() || !thumbnailDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
					originalDirectory.mkdirs(); //생성할 폴더가 하나이면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
					thumbnailDirectory.mkdirs();
				}
				/* 최종적으로 request를 parsing하고 파일을 저장한 뒤 필요한 내용을 담을 리스트와 맵
				 * 파일에 대한 정보는 리스트에, 다른 파라미터의 정보는 모두 맵에 담을 것임
				 * */
				Map<String, String> fileMap = new HashMap<>();
				List<Map<String, String>> fileList = new ArrayList<>();
				
				for(MultipartFile file : files) {
					UUID uuid = UUID.randomUUID(); //랜덤 문자 생성
					
					String origFileName = file.getOriginalFilename(); //원본파일명
					String extension = FilenameUtils.getExtension(origFileName); //확장자
					String randomFileName = uuid.toString().replace("-", "") + "." + extension; //랜덤파일명
					
					try {
						//원본 크기 파일을 original 폴더에 저장
						File target = new File(originalUploadPath, randomFileName);
						byte[] bytes = file.getBytes();
						FileCopyUtils.copy(bytes, target);
						
						String origFileUrl = "/upload/inquiry/original/" + uuid.toString().replace("-", "") + "." + extension;
						fileMap.put("origFileName", origFileName);
						fileMap.put("saveFileName", randomFileName);
						fileMap.put("savePath", origFileUrl);
						
						//썸네일 파일을 thumbnail 폴더에 저장
						Thumbnails.of(originalUploadPath + File.separator + randomFileName) //썸네일로 변환 후 저장
						.size(THUMB_WIDTH_SIZE, THUMB_HEIGHT_SIZE)
						.toFile(thumbnailUploadPath + File.separator + "thumbnail_" + randomFileName);
						fileMap.put("thumbnailPath", "/upload/inquiry/thumbnail/thumbnail_" + randomFileName); //웹서버에서 접근 가능한 형태로 썸네일의 저장 경로 작성
						
						fileList.add(fileMap);
						
						//현재 문의번호 조회
						int currInquiryNo = csService.checkCurrInquiryNo(); //새 글 작성 중 부여된 번호 조회
						
						AttachmentDTO tempFileInfo = new AttachmentDTO();
						for(int i=0; i < fileList.size(); i++) {
							tempFileInfo.setRefInqNo(currInquiryNo);
							tempFileInfo.setOrigFileName(fileList.get(i).get("origFileName"));
							tempFileInfo.setSaveFileName(fileList.get(i).get("saveFileName"));
							tempFileInfo.setSavePath(fileList.get(i).get("savePath"));
							tempFileInfo.setThumbnailPath(fileList.get(i).get("thumbnailPath"));
							
							if(i == 0) { //index 기준으로 첫번째 첨부 이미지는 메인썸네일, 그 다음은 서브썸네일에 해당
								tempFileInfo.setFileType("THUMB_MAIN");
							} else {
								tempFileInfo.setFileType("THUMB_SUB");
							}
						}
						fileUploaded = csService.attachInquiryImages(tempFileInfo);
						
						int currAttachNo = downloadService.checkCurrAttachNo();
						setDownloadCount += downloadService.setDownloadCount(currAttachNo); //첨부파일 기본 다운로드횟수 세팅
						
						fileCount += fileUploaded;
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			
			if(fileUploaded == fileCount && fileCount == setDownloadCount) {
				rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("inquiryPostedSuccessfully", null, locale));
			} else {
				rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("errorWhileUploadingInquiryFiles", null, locale));
			}
		} else {
			rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("errorWhilePostingAInquiry", null, locale));
		}
		return "redirect:/cs/main";
	}
	
	/**
	 * 1:1 문의 상세 페이지
	 */
	@GetMapping("inquiry/details")
	public void getInquiryDetails(@RequestParam("no") int inquiryNo, HttpSession session, Model model) {
		log.info("inquiry details 요청 : {}", inquiryNo);
		String memberId = (String) session.getAttribute("loginMember");
		InquiryDTO inquiryDetail =  csService.getInquiryDetails(memberId, inquiryNo); //본인확인에 필요한 memberId
		if(inquiryDetail == null) {
			log.info("접근권한 없는 inquiry 요청");
		} else {
			model.addAttribute("inquiryDetail", inquiryDetail);
		}
	}
}
