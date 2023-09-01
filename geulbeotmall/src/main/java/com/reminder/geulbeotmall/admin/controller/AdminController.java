package com.reminder.geulbeotmall.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.reminder.geulbeotmall.admin.model.dto.MemberSuspDTO;
import com.reminder.geulbeotmall.admin.model.dto.SuspDTO;
import com.reminder.geulbeotmall.admin.model.dto.TrashDTO;
import com.reminder.geulbeotmall.admin.model.service.AdminService;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.service.OrderService;
import com.reminder.geulbeotmall.community.model.dto.CommentDTO;
import com.reminder.geulbeotmall.community.model.service.CommentService;
import com.reminder.geulbeotmall.cs.model.dto.InquiryDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.notification.model.dto.Notification;
import com.reminder.geulbeotmall.notification.model.dto.NotificationType;
import com.reminder.geulbeotmall.notification.model.service.NotificationService;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;
import com.reminder.geulbeotmall.paging.model.dto.PageDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;
import com.reminder.geulbeotmall.upload.model.dto.DesignImageDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@SessionAttributes({"loginMember"})
public class AdminController {
	
	private final AdminService adminService;
	private final ProductService productService;
	private final OrderService orderService;
	private final CommentService commentService;
	private final NotificationService notificationService;
	private final MessageSource messageSource;
	
	@Autowired
	public AdminController(AdminService adminService, ProductService productService, OrderService orderService,
			CommentService commentService, NotificationService notificationService, MessageSource messageSource) {
		this.adminService = adminService;
		this.productService = productService;
		this.orderService = orderService;
		this.commentService = commentService;
		this.notificationService = notificationService;
		this.messageSource = messageSource;
	}
	
	@GetMapping("/dashboard")
	public String getDashboard(Model model) {
		/* 복구기한 100일이 경과한 휴지통 삭제글 영구 삭제 */
		List<Map<String, Integer>> postNoList = adminService.getTrashItemToDelete();
		log.info("금일 기준 '영구 삭제 대상 휴지통 삭제글' 개수 : {}", postNoList.size());
		
		int count = 0;
		for(int i=0; i < postNoList.size(); i++) {
			String refBoard = String.valueOf(postNoList.get(i).get("REF_BOARD")); //컬럼명 기준으로 값 호출
			int refPostNo = Integer.parseInt(String.valueOf(postNoList.get(i).get("REF_POST_NO")));
			int result = adminService.permanentlyDeleteFromTrashAndOriginalTableData(refBoard, refPostNo);
			if(result == 1) count++;
		}
		log.info("영구 삭제글 count : {}", count);
		
		/* 통계 자료 조회(기본 조회 기간 '최근 일주일') */
		String range = "oneWeek";
		String start = "";
		String end = "";
		
		List<Map<String, Integer>> memberData = adminService.getMemberDataByDate(range, start, end); //회원수
		List<Map<String, Integer>> salesData = adminService.getSalesDataByDate(range, start, end); //매출액
		List<Map<ProductDTO, Integer>> top8Product = adminService.getTopSalesProduct(range, start, end); //누적 판매량 Top 8
		model.addAttribute("memberData", memberData);
		model.addAttribute("salesData", salesData);
		model.addAttribute("top8Product", top8Product);
		return "admin/dashboard";
	}
	
	/* 통계 데이터 */
	@PostMapping(value="/dashboard/statistics", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView getDashboardWithSelectedRange(@RequestBody Map<String, String> params) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		log.info("dashboard statistics params: {}", params);
		
		String range = params.get("range").toString();
		String start = params.get("start").toString();
		String end = params.get("end").toString();
		List<Map<String, Integer>> memberData = adminService.getMemberDataByDate(range, start, end); //회원수
		List<Map<String, Integer>> salesData = adminService.getSalesDataByDate(range, start, end); //매출액
		List<Map<ProductDTO, Integer>> top8Product = adminService.getTopSalesProduct(range, start, end); //누적 판매량 Top 8
		mv.addObject("memberData", memberData);
		mv.addObject("salesData", salesData);
		mv.addObject("top8Product", top8Product);
		return mv;
	}
	
	/* 디자인관리 */
	@GetMapping("/design")
	public String getDesignPage(Model model) {
		List<ProductDTO> allProducts = productService.getProductsToDisplay();
		model.addAttribute("allProducts", allProducts);
		return "admin/design";
	}
	
	@PostMapping(value="/design", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public String addDisplayImages(@RequestParam("files") List<MultipartFile> files, @RequestParam(value="refArr", required=false) String[] refArr, HttpServletRequest request,
			RedirectAttributes rttr, Locale locale) {
		log.info("메인 이미지 추가 시작");

		String realPath = request.getSession().getServletContext().getRealPath("/");
		log.info("src/main/webapp : {}", realPath);
		
		String originalUploadPath = realPath + "upload" + File.separator + "main" + File.separator + "original";
		File originalDirectory = new File(originalUploadPath);
		
		if(!originalDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
			originalDirectory.mkdirs(); //생성할 폴더가 하나이면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
		}
		
		/* 최종적으로 request를 parsing하고 파일을 저장한 뒤 필요한 내용을 담을 리스트와 맵
		 * 파일에 대한 정보는 리스트에, 다른 파라미터의 정보는 모두 맵에 담을 것임
		 * */
		Map<String, String> fileMap = new HashMap<>();
		List<Map<String, String>> fileList = new ArrayList<>();
		int count = 0;
		int index = 0;
		
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
				
				String origFileUrl = "/upload/main/original/" + uuid.toString().replace("-", "") + "." + extension;
				fileMap.put("origFileName", origFileName);
				fileMap.put("saveFileName", randomFileName);
				fileMap.put("savePath", origFileUrl);
				
				fileList.add(fileMap);
				
				DesignImageDTO tempFileInfo = new DesignImageDTO();
				tempFileInfo.setOrigImageName(fileList.get(index).get("origFileName"));
				tempFileInfo.setSaveImageName(fileList.get(index).get("saveFileName"));
				tempFileInfo.setSavePath(fileList.get(index).get("savePath"));
				/* 배너 */
				if(refArr[0].equals("banner")) {
					tempFileInfo.setImageType("BANNER");
				/* 슬라이더 */
				} else {
					tempFileInfo.setImageType("SLIDER");
					tempFileInfo.setRefProdNo(Integer.parseInt(refArr[index]));
				}
				int result = adminService.addDisplayImages(tempFileInfo);
				count += result;
				index++;
			} catch (IOException e) { e.printStackTrace(); }
		}
		if(count == files.size()) {
			rttr.addFlashAttribute("displayImageMessage", messageSource.getMessage("imageAddedSuccessfully", null, locale));
		} else {
			rttr.addFlashAttribute("displayImageMessage", messageSource.getMessage("errorWhileAddingAnImage", null, locale));
		}
		return "redirect:/admin/design";
	}
	
	/* 회원관리 */
	@GetMapping("/member/list")
	public void getMemberList(@Valid @ModelAttribute("criteria") Criteria criteria, BindingResult bindingResult, HttpServletRequest request, Model model) {
		log.info("회원 목록 요청");
		
		int total = adminService.getTotalNumber(criteria);
		int regular = adminService.getRegularNumber(criteria);
		int admin = adminService.getAdminNumber(criteria);
		int closed = adminService.getClosedNumber(criteria);
		log.info("전체 회원수 : {}", total);
		log.info("일반 회원수 : {}", regular);
		log.info("관리자 수 : {}", admin);
		
		List<MemberDTO> memberList = adminService.getMemberList(criteria);
		List<MemberDTO> memberOnly = adminService.getMemberOnly(criteria);
		List<MemberDTO> adminOnly = adminService.getAdminOnly(criteria);
		List<MemberSuspDTO> closedOnly = adminService.getClosedOnly(criteria);
		for(int i=0; i < closedOnly.size(); i++) {
			boolean isClosedByUser = closedOnly.get(i).getSusp().getAccSuspDesc() == null ? true : false;
			if(isClosedByUser) {
				closedOnly.get(i).getSusp().setAccSuspDesc("자진탈퇴");
			}
		}
		log.info("회원 목록 조회 완료");
		
		model.addAttribute("total", total);
		model.addAttribute("regular", regular);
		model.addAttribute("admin", admin);
		model.addAttribute("closed", closed);
		model.addAttribute("memberList", memberList);
		model.addAttribute("memberOnly", memberOnly);
		model.addAttribute("adminOnly", adminOnly);
		model.addAttribute("closedOnly", closedOnly);
		model.addAttribute("pageMaker", new PageDTO(total, 10, criteria));
		model.addAttribute("pageMakerWithRegularMemberNumber", new PageDTO(regular, 10, criteria));
		model.addAttribute("pageMakerWithAdminNumber", new PageDTO(admin, 10, criteria));
		model.addAttribute("pageMakerWithClosedMemberNumber", new PageDTO(closed, 10, criteria));
	}
	
	@GetMapping("/member/details")
	public String getMemberDetails(@RequestParam("id") String memberId, Model model) {
		MemberDTO detail = adminService.getMemberDetails(memberId);
		List<SuspDTO> susp = adminService.getSuspDetails(memberId);
		int suspCount = adminService.getSuspCount(memberId);
		model.addAttribute("detail", detail);
		model.addAttribute("susp", susp);
		model.addAttribute("suspCount", suspCount);
		return "admin/member/details";
	}
	
	@PostMapping(value="/member/manageAuth", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String changeAuth(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		log.info("권한 수정 시작");
		String optValue = params.get("optValue").toString();
		int selected = Integer.parseInt(optValue);
		String[] idList = request.getParameterValues("arr");
		int count = 0;
		String result = "";
		
		for(int i=0; i < idList.length; i++) {
			int current = adminService.searchAuthById(idList[i]);
			log.info("현재 권한 수(1 : 일반회원, 2 : 관리자) : {}", current);
			
			if(current == 2 && selected == 2) {
				result = idList[i] + "(은)는 이미 관리자입니다";
				log.info(result);
			} else if(current == 1 && selected == 1) {
				result = idList[i] + "(은)는 이미 일반회원입니다";
				log.info(result);
			} else if(current == 2 && selected == 1) { //현재 관리자에서 일반회원으로 변경
				count += adminService.deleteAuthAsAdmin(idList[i]);
			} else { //현재 일반회원에서 관리자로 변경
				count += adminService.insertAuthAsAdmin(idList[i]);
			}
			
			if(idList.length == count) {
				result = "성공";
				log.info(result);
			}
		}
		return result;
	}
	
	@PostMapping(value="/member/suspendAcc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String updateAccSuspension(@RequestParam Map<String, Object> params) {
		String memberId = params.get("memberId").toString();
		String accSuspDesc = params.get("accSuspDesc").toString();
		int resultA = adminService.updateAccSuspension(memberId);
		int resultB = adminService.insertAccSuspension(memberId, accSuspDesc);
		
		String result = "";
		if(resultA == 1 && resultB == 1) {
			result = "성공";
		}
		return result;
	}
	
	@PostMapping(value="/member/activateAcc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String updateAccActivation(HttpServletRequest request) {
		String[] idList = request.getParameterValues("arr");
		int count = 0;
		String result = "";
		
		for(int i=0; i < idList.length; i++) {
			adminService.updateAccActivation(idList[i]);
			count++;
		}
		
		if(idList.length == count) {
			result = "성공";
		}
		return result;
	}
	
	/* 게시글관리(리뷰,문의,휴지통) */
	@GetMapping("/post/list")
	public void getPostList(@Valid @ModelAttribute("criteria") Criteria criteria, Model model) {
		int inquiry = adminService.getTotalInquiryNumber(criteria);
		int review = adminService.getTotalReviewNumber(criteria);
		List<InquiryDTO> totalInquiryList = adminService.getTotalInquiryPostList(criteria);
		List<ReviewDTO> totalReviewList = adminService.getTotalReviewPostList(criteria);
		
		String checkTrashRefBoard = "post"; //휴지통 삭제글 조회용
		int trash = adminService.getTotalTrashNumber(criteria, checkTrashRefBoard);
		List<Map<TrashDTO, String>> totalTrashList = adminService.getTotalTrashList(criteria, checkTrashRefBoard);
		
		model.addAttribute("inquiry", inquiry);
		model.addAttribute("review", review);
		model.addAttribute("trash", trash);
		model.addAttribute("totalInquiryList", totalInquiryList);
		model.addAttribute("totalReviewList", totalReviewList);
		model.addAttribute("totalTrashList", totalTrashList);
		model.addAttribute("pageMakerWithTotalInquiryNumber", new PageDTO(inquiry, 10, criteria));
		model.addAttribute("pageMakerWithTotalReviewNumber", new PageDTO(review, 10, criteria));
		model.addAttribute("pageMakerWithTotalTrashNumber", new PageDTO(trash, 10, criteria));
	}
	@PostMapping("/post/restorePost")
	@ResponseBody
	public String restorePostsFromTrash(HttpServletRequest request) {
		String[] trashNoArr = request.getParameterValues("arr");
		int count = 0;
		for(int i=0; i < trashNoArr.length; i++) {
			int result = adminService.restoreAPostFromTrash(Integer.parseInt(trashNoArr[i]));
			if(result == 1) count++;
		}
		return count == trashNoArr.length ? "succeed" : "fail";
	}
	@PostMapping("/post/moveToTrash")
	@ResponseBody
	public String movePostsToTrash(@RequestParam("admin") String admin, HttpServletRequest request) {
		String[] postNoArr = request.getParameterValues("noArr");
		String[] postWriterArr = request.getParameterValues("writerArr");
		String[] refBoardArr = request.getParameterValues("boardArr");
		log.info("게시글 삭제자 admin : {}", admin);
		int count = 0;
		
		TrashDTO trashDTO = new TrashDTO();
		trashDTO.setTrashDeleteBy(admin);
		for(int i=0; i < postNoArr.length; i++) {
			trashDTO.setRefBoard(refBoardArr[i]);
			if(refBoardArr[i].equals("comment")) {
				commentService.deleteAComment(Integer.parseInt(postNoArr[i])); //댓글삭제일자 반영
			}
			trashDTO.setRefPostNo(Integer.parseInt(postNoArr[i]));
			trashDTO.setTrashWriter(postWriterArr[i]);
			int result = adminService.moveAPostToTrash(trashDTO);
			if(result == 1) count++;
		}
		return count == postNoArr.length ? "succeed" : "fail";
	}
	
	/* 댓글관리(전체,휴지통) */
	@GetMapping("/comment/list")
	public void getCommentList(@Valid @ModelAttribute("criteria") Criteria criteria, Model model) {
		int total = adminService.getTotalCommentNumber(criteria);
		List<Map<CommentDTO, String>> totalCommentList = adminService.getTotalCommentList(criteria); //CASE 통해 REF_BOARD마다 TITLE을 선택해 가져오므로 List<Map<CommentDTO, String>> 타입으로 정의
		
		String checkTrashRefBoard = "comment"; //휴지통 삭제댓글 조회용
		int trash = adminService.getTotalTrashNumber(criteria, checkTrashRefBoard);
		List<Map<TrashDTO, String>> totalTrashList = adminService.getTotalTrashList(criteria, checkTrashRefBoard);
		
		model.addAttribute("total", total);
		model.addAttribute("trash", trash);
		model.addAttribute("totalCommentList", totalCommentList);
		model.addAttribute("totalTrashList", totalTrashList);
		model.addAttribute("pageMaker", new PageDTO(total, 10, criteria));
		model.addAttribute("pageMakerWithTotalTrashNumber", new PageDTO(trash, 10, criteria));
	}
	
	/* 배송관리 */
	@GetMapping("/order/list")
	public void getOrderList(@Valid @ModelAttribute("criteria") Criteria criteria, Model model) {
		log.info("주문/배송 목록 요청");
		
		int total = adminService.getTotalOrderNumber(criteria);
		int preparing = adminService.getPreparingOrderNumber(criteria);
		int delivering = adminService.getDeliveringOrderNumber(criteria);
		int completed = adminService.getCompletedOrderNumber(criteria);
		
		Map<String, Integer> numberOfEachOrder = new HashMap<>();
		String orderNo = "";
		int number = 0;
		List<OrderDetailDTO> totalOrderList = adminService.getTotalOrderList(criteria);
		for(int i=0; i < totalOrderList.size(); i++) {
			orderNo = totalOrderList.get(i).getOrder().getOrderNo();
			number = orderService.getTheNumberOfEachOrder(orderNo);
			numberOfEachOrder.put(orderNo, number);
		}
		List<OrderDetailDTO> preparingOnly = adminService.getPreparingOnly(criteria);
		List<OrderDetailDTO> deliveringOnly = adminService.getDeliveringOnly(criteria);
		List<OrderDetailDTO> completedOnly = adminService.getCompletedOnly(criteria);
		log.info("주문/배송 목록 조회 완료");
		
		model.addAttribute("total", total);
		model.addAttribute("preparing", preparing);
		model.addAttribute("delivering", delivering);
		model.addAttribute("completed", completed);
		model.addAttribute("numberOfEachOrder", numberOfEachOrder);
		model.addAttribute("totalOrderList", totalOrderList);
		model.addAttribute("preparingOnly", preparingOnly);
		model.addAttribute("deliveringOnly", deliveringOnly);
		model.addAttribute("completedOnly", completedOnly);
		model.addAttribute("pageMaker", new PageDTO(total, 10, criteria));
		model.addAttribute("pageMakerWithPreparingOrderNumber", new PageDTO(preparing, 10, criteria));
		model.addAttribute("pageMakerWithDeliveringOrderNumber", new PageDTO(delivering, 10, criteria));
		model.addAttribute("pageMakerWithCompletedOrderNumber", new PageDTO(completed, 10, criteria));
	}

	@PostMapping(value="/order/manageDeliveryStatus", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String changeDlvrStatus(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		log.info("배송상태 변경 시작");
		String dlvrStatus = params.get("dlvrValue").toString();
		String[] orderList = request.getParameterValues("arr");
		boolean isCommited = false;
		String result = "";
		List<Notification> notificationList = new ArrayList<>();
		
		for(int i=0; i < orderList.length; i++) {
			isCommited = adminService.updateDeliveryStatus(dlvrStatus, orderList[i]);
			
			/* SSE 통신 및 DB 저장(상품출고 또는 배송완료 알림) */
			OrderDetailDTO orderDetail = adminService.getOrderDetailsByOrderNo(orderList[i]);
			String receiver = orderDetail.getMemberId();
			String notificationId = receiver + "_" + System.currentTimeMillis();
			String content = "";
			String type = "";
			String url = "";
			if(dlvrStatus.equals("배송중")) {
				content = "🚚주문하신 상품이 출고되었습니다. 안전하게 배송해드릴게요";
				type = NotificationType.DISPATCH.getAlias();
				url = NotificationType.DISPATCH.getPath() + orderList[i];
			} else {
				content = "🏡배송이 완료되었습니다. 리뷰를 작성하고 적립금 혜택을 받아보세요";
				type = NotificationType.DELIVERY.getAlias();
				url = NotificationType.DELIVERY.getPath();
			}
			Notification notification = Notification.builder()
					.notificationId(notificationId)
					.receiver(receiver)
					.content(content)
					.notificationType(type)
					.url(url)
					.readYn('N')
					.deletedYn('N')
					.build();
			notificationList.add(notification);
			log.info("dispatch/delivery notification added : {}", notification);
		}
		
		if(isCommited) {
			notificationService.sendNotifications(notificationList);
			result = "succeed";
		}
		return result;
	}
	
	@GetMapping("/order/details")
	public String getOrderDetails(@RequestParam("no") String orderNo, Model model) {
		OrderDetailDTO detail = adminService.getOrderDetailsByOrderNo(orderNo);
		String method = detail.getPayment().getPaymentMethod();
		switch(method) {
			case "card": method = "신용카드"; break;
			case "trans": method = "실시간계좌이체"; break;
			case "vbank": method = "가상계좌"; break;
			case "phone": method = "휴대폰결제"; break;
		}
		detail.getPayment().setPaymentMethod(method);
		model.addAttribute("detail", detail);
		return "admin/order/details";
	}
}
