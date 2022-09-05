package com.reminder.geulbeotmall.review.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.review.model.service.ReviewService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Controller
@RequestMapping("/review")
public class ReviewController {

	//썸네일 크기
	public static final int THUMB_WIDTH_SIZE = 540;
	public static final int THUMB_HEIGHT_SIZE = 540;
	
	private final ReviewService reviewService;
	private final MemberService memberService;
	private final MessageSource messageSource;
	
	@Autowired
	public ReviewController(ReviewService reviewService, MemberService memberService, MessageSource messageSource) {
		this.reviewService = reviewService;
		this.memberService = memberService;
		this.messageSource = messageSource;
	}

	@GetMapping("/write") /* @{/review/write(order=${itemList.orderNo}, option=${itemList.optionNo})} */
	public void reviewWriteForm(@RequestParam("order") String orderNo, @RequestParam("option") int optionNo, @AuthenticationPrincipal UserImpl user, Model model) {
		log.info("리뷰 작성 요청 주문번호 및 옵션번호 : {}", orderNo, optionNo);
		OrderDTO reviewOption = memberService.postAReview(user.getMemberId(), orderNo, optionNo);
		String orderDate = reviewOption.getOrderDetail().getOrderDate().substring(0, 10).replaceAll("\\.", "\\-");
		reviewOption.getOrderDetail().setOrderDate(orderDate);
		model.addAttribute("reviewOption", reviewOption);
	}
	
	@PostMapping(value="/write", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public String writeAReview(@Validated @ModelAttribute("review") ReviewDTO reviewDTO, @AuthenticationPrincipal UserImpl user, 
			@RequestParam(value="files", required=false) List<MultipartFile> files, HttpServletRequest request, RedirectAttributes rttr, Locale locale) {
		log.info("review title : {}", reviewDTO.getRevwTitle());
		log.info("review rating : {}", reviewDTO.getRevwRatings());
		log.info("review content : {}", reviewDTO.getRevwContent());
		log.info("review orderNo : {}", reviewDTO.getOrderNo());
		log.info("review optionNo : {}", reviewDTO.getOptionNo());
		reviewDTO.setMemberId(user.getMemberId());
		int posting = reviewService.postAReview(reviewDTO);
		
		PointDTO pointDTO = new PointDTO();
		String paymentNo = reviewService.getPaymentNoByOrderNo(reviewDTO.getOrderNo());
		pointDTO.setPaymentNo(paymentNo);
		pointDTO.setPointStatus("적립");
		
		if(!files.get(0).getOriginalFilename().equals("")) {
			log.info("리뷰 사진 추가 시작");
			
			String realPath = request.getSession().getServletContext().getRealPath("/");
			log.info("src/main/webapp : {}", realPath);
			
			String originalUploadPath = realPath + "upload" + File.separator + "review" + File.separator + "original";
			String thumbnailUploadPath = realPath + "upload" + File.separator + "review" + File.separator + "thumbnail";
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
			int count = 0;
			
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
					
					String origFileUrl = "/upload/review/original/" + uuid.toString().replace("-", "") + "." + extension;
					fileMap.put("origFileName", origFileName);
					fileMap.put("saveFileName", randomFileName);
					fileMap.put("savePath", origFileUrl);
					
					//썸네일 파일을 thumbnail 폴더에 저장
					Thumbnails.of(originalUploadPath + File.separator + randomFileName) //썸네일로 변환 후 저장
					.size(THUMB_WIDTH_SIZE, THUMB_HEIGHT_SIZE)
					.toFile(thumbnailUploadPath + File.separator + "thumbnail_" + randomFileName);
					fileMap.put("thumbnailPath", "/upload/review/thumbnail/thumbnail_" + randomFileName); //웹서버에서 접근 가능한 형태로 썸네일의 저장 경로 작성
					
					fileList.add(fileMap);
					
					//현재 리뷰번호 조회
					int currReviewNo = reviewService.checkCurrReviewNo();
					
					AttachmentDTO tempFileInfo = new AttachmentDTO();
					for(int i=0; i < fileList.size(); i++) {
						tempFileInfo.setRefRevwNo(currReviewNo);
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
					int result = reviewService.attachReviewImages(tempFileInfo);
					count += result;
				} catch (IOException e) { e.printStackTrace(); }
			}
			/* 적립금A. 포토리뷰 작성 시 300원 적립 */
			pointDTO.setPointAmount(300);
			int point300 = reviewService.savePoints(pointDTO);
			if(posting == 1 && count == files.size() && point300 == 1) {
				rttr.addFlashAttribute("successMessage", messageSource.getMessage("reviewPostedSuccessfully", null, locale));
			} else {
				rttr.addFlashAttribute("errorMessage", messageSource.getMessage("errorWhilePostingAReview", null, locale));
			}
		} else {
			/* 적립금B. 상품리뷰 작성 시 100원 적립 */
			pointDTO.setPointAmount(100);
			int point100 = reviewService.savePoints(pointDTO);
			if(posting == 1 && point100 == 1) {
				rttr.addFlashAttribute("successMessage", messageSource.getMessage("reviewPostedSuccessfully", null, locale));
			} else {
				rttr.addFlashAttribute("errorMessage", messageSource.getMessage("errorWhilePostingAReview", null, locale));
			}
		}
		return "redirect:/mypage/review";
	}
}
