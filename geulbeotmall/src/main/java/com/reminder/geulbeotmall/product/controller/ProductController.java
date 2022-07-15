package com.reminder.geulbeotmall.product.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.dto.StockDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Controller
public class ProductController {
	
	private final ProductService productService;
	private final MessageSource messageSource;
	
	@Autowired
	public ProductController(ProductService productService, MessageSource messageSource) {
		this.productService = productService;
		this.messageSource = messageSource;
	}
	
	/**
	 * 상품등록 페이지 이동
	 */
	@GetMapping("/admin/product/add")
	public void addProduct(Model model) {
		/* 카테고리 목록 호출 */
		List<CategoryDTO> category = productService.getCategoryList();
		model.addAttribute("category", category);
		
		/* 브랜드 목록 호출 */
		List<BrandDTO> brand = productService.getBrandList();
		model.addAttribute("brand", brand);
	}
	
	/**
	 * 카테고리 중복 검사 및 새 카테고리 추가
	 */
	@PostMapping(value="/admin/product/checkCategory", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView checkCategoryName(@RequestBody Map<String, String> param, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		/* 카테고리 중복 검사 */
		String categoryName = param.get("category").replaceAll("\\s", ""); //문자 사이 공백제거 후 비교
		log.info(categoryName);
		int categoryCount = productService.checkCategoryName(categoryName);
		log.info("카테고리 중복 조회 결과 : {}", categoryCount);
		
		if(categoryCount > 0) { //이미 존재하는 카테고리
			String errorMessage = messageSource.getMessage("errorWhileAddingANewCategory", null, locale);
			mv.addObject("errorMessage", errorMessage);
			log.info(errorMessage);
		}
		
		if(categoryCount == 0) { //새 카테고리 추가
			int result = productService.addANewCategory(categoryName);
			if(result == 1) {
				log.info("새 카테고리 추가 완료 : {}", categoryName);
			}
		}
		return mv;
	}
	
	/**
	 * 브랜드 중복 검사 및 새 브랜드 추가
	 */
	@PostMapping(value="/admin/product/checkBrand", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView checkBrandName(@RequestBody Map<String, String> param, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		/* 브랜드 중복 검사 */
		String brandName = param.get("brand").replaceAll("\\s", ""); //문자 사이 공백제거 후 비교
		log.info(brandName);
		int brandCount = productService.checkBrandName(brandName);
		log.info("브랜드 중복 조회 결과 : {}", brandCount);
		
		if(brandCount > 0) { //이미 존재하는 브랜드
			String errorMessage = messageSource.getMessage("errorWhileAddingANewBrand", null, locale);
			mv.addObject("errorMessage", errorMessage);
			log.info(errorMessage);
		}
		
		if(brandCount == 0) { //새 브랜드 추가
			int result = productService.addANewBrand(brandName);
			if(result == 1) {
				log.info("새 브랜드 추가 완료 : {}", brandName);
			}
		}
		return mv;
	}
	
	/**
	 * 새 상품 및 상품 썸네일 등록
	 */
	@PostMapping("/admin/product/add")
	@ResponseBody
	public ModelAndView addProduct(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		log.info("상품 썸네일 추가 시작");
		
		String realPath = request.getSession().getServletContext().getRealPath("/");
		log.info("src/main/webapp : {}", realPath);
		
		String originalUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "original";
		String thumbnailUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "thumbnail";
		File originalDirectory = new File(originalUploadPath);
		File thumbnailDirectory = new File(thumbnailUploadPath);
		
		if(!originalDirectory.exists() || !thumbnailDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
			originalDirectory.mkdirs(); //생성할 폴더가 하나이면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
			thumbnailDirectory.mkdirs();
		}
		
		
		/* parsing 된 request 파일을 저장한 뒤
		 * 파일에 대한 정보는 List에, 다른 파라미터 정보는 모두 Map에 담김 */
		Map<String, String> parameter = new HashMap<>();
		List<Map<String, String>> fileList = new ArrayList<>();
		
		int maxFileSize = 1024 * 1024 * 10;
		
		/* 파일을 업로드할 때 최대 크기, 임시 저장할 폴더의 경로 등을 포함하기 위한 인스턴스 */
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(new File(originalUploadPath));
		fileItemFactory.setSizeThreshold(maxFileSize);
		
		ServletFileUpload fileUpload = new ServletFileUpload();
		
		try {
			List<FileItem> fileItems = fileUpload.parseRequest(request);
			
			for(FileItem item : fileItems) {
				log.info(item.toString());
			}
			
			for(int i=0; i < fileItems.size(); i++) {
				FileItem item = fileItems.get(i);
				
				if(!item.isFormField()) {
					if(item.getSize() > 0) {
						String filedName = item.getFieldName();
						String origFileName = item.getName();
						
						int dot = origFileName.lastIndexOf(".");
						String extension = origFileName.substring(dot);
						
						String randomFileName = UUID.randomUUID().toString().replace("-", "") + extension;
						
						File storeFile = new File(originalUploadPath + "/" + randomFileName);
						
						item.write(storeFile);
						
						Map<String, String> fileMap = new HashMap<>();
						fileMap.put("filedName", filedName);
						fileMap.put("origFileName", origFileName);
						fileMap.put("saveFileName", randomFileName);
						fileMap.put("savePath", originalUploadPath);
						
						int width = 0;
						int height = 0;
						if("thumbnail".equals(filedName)) {
							fileMap.put("fileType", "THUMB");
							
							width = 200;
							height = 200;
						} else {
							fileMap.put("fileType", "CONTENT");
						}
						
						Thumbnails.of(originalUploadPath + randomFileName)
								  .size(width, height)
								  .toFile(thumbnailDirectory + "s_" + randomFileName);
						
						fileMap.put("thumbnailPath", "/resources/upload/thumbnail/s_" + randomFileName);
						
						fileList.add(fileMap);
					}
				} else {
					parameter.put(item.getFieldName(), new String(item.getString().getBytes("ISO-8859-1"), "UTF-8"));
				}
			}
			
			log.info("parameter : {}", parameter);
			log.info("fileList : {}", fileList);
			
			ProductDTO thumbnail = new ProductDTO();
			thumbnail.setAttachmentList(new ArrayList<AttachmentDTO>());
			List<AttachmentDTO> list = thumbnail.getAttachmentList();
			for(int i=0; i < fileList.size(); i++) {
				Map<String, String> file = fileList.get(i);
				
				AttachmentDTO tempFileInfo = new AttachmentDTO();
				tempFileInfo.setOrigFileName(file.get("origFileName"));
				tempFileInfo.setSaveFileName(file.get("saveFileName"));
				tempFileInfo.setSavePath(file.get("savePath"));
				tempFileInfo.setFileType(file.get("fileType"));
				tempFileInfo.setThumbnailPath(file.get("thumbnailPath"));
				
				list.add(tempFileInfo);
			}
			
			log.info("product thumbnail : {}" + thumbnail);
			
			int thumbResult = productService.attachProdThumbnail(thumbnail);
		} catch (Exception e) {

			int count = 0;
			for(int i=0; i < fileList.size(); i++) {
				Map<String, String> file = fileList.get(i);
				
				File deleteFile = new File(originalUploadPath + "/" + file.get("saveFileName"));
				boolean isDeleted = deleteFile.delete();
				
				if(isDeleted) count++;
			}
			
			if(count == fileList.size()) {
				log.info("업로드에 실패한 모든 사진 삭제 완료");
				e.printStackTrace();
			} else {
				e.printStackTrace();
			}
		}
		
		//상품 추가
		String categoryName = params.get("category").toString();
		int categoryNo = productService.checkCategoryNo(categoryName);
		String prodName = params.get("prodName").toString();
		String prodDesc = params.get("prodDesc").toString();
		String productTag = params.get("tagArr").toString().replace("$", ",");
		int discountRate = Integer.parseInt(params.get("discountRate").toString());
		int prodPrice = Integer.parseInt(params.get("prodPrice").toString());
		String brandName = params.get("brand").toString();
		int brandNo = productService.checkBrandNo(brandName);
		String prodOrigin = params.get("prodOrigin").toString();
		String prodDetailContent = params.get("prodDetailContent").toString();
		int result = productService.addProduct(categoryNo, prodName, prodDesc, productTag, discountRate, prodPrice, brandNo, prodOrigin, prodDetailContent);
		
		int totalOptionNumber = Integer.parseInt(params.get("optArrLength").toString());
		log.info("사용자가 추가한 총 옵션 개수 : {}", totalOptionNumber);
		
		String[] bodyColors = params.get("bodyColor").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] inkColors = params.get("inkColor").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] pointSizes = params.get("pointSize").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] stockAmounts = params.get("stockAmount").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] extraCharges = params.get("extraCharge").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		
		StockDTO stock = new StockDTO();
		OptionDTO option = new OptionDTO();
		
		for(int i=0; i < totalOptionNumber; i++) {
			
			//입고 추가
			int amount = Integer.parseInt(stockAmounts[i].toString());
			productService.addProductStock(amount);
			
			//옵션 추가
			String bodyColor = bodyColors[i].toString();
			String inkColor = inkColors[i].toString();
			double pointSize = Double.parseDouble(pointSizes[i].toString());
			int extraCharge = Integer.parseInt(extraCharges[i].toString());
			
			option.setBodyColor(bodyColor);
			option.setInkColor(inkColor);
			option.setPointSize(pointSize);
			option.setExtraCharge(extraCharge);
			
			productService.addProductOption(option.getBodyColor(), option.getInkColor(), option.getPointSize(), option.getExtraCharge());
		}
		
		if(result == 1) {
			mv.addObject("errorMessage", "성공");
		} else {
			mv.addObject("errorMessage", "오류 발생");
		}
		
		return mv;
	}
	
	/**
	 * 상품 상세내용이미지(CKEditor4 첨부 이미지) 업로드
	 */
	@PostMapping("/admin/product/add/contentImageUpload")
	public void uploadProdContentImage(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
		
		OutputStream out = null; //지정된 공간에 이미지 파일들을 내보내기하여 저장
		PrintWriter printWriter = null; //View에 띄워줄 구문 작성
		
		response.setCharacterEncoding("UTF-8"); //인코딩
		response.setContentType("text/html; charset=UTF-8");
		
		try {
			//Spring Boot 개발 환경에서는 src/main 하위에 webapp 폴더를 직접 생성해야 함
			String realPath = request.getSession().getServletContext().getRealPath("/");
			log.info("src/main/webapp : {}", realPath);
			String imageUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "content";
			File contentImageDirectory = new File(imageUploadPath);
			if(!contentImageDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
				contentImageDirectory.mkdir();
			}
			
			UUID uuid = UUID.randomUUID(); //랜덤 문자 생성
			
			byte[] bytes = upload.getBytes();
			
			String origFileName = upload.getOriginalFilename(); //원본파일명
			String extension = FilenameUtils.getExtension(origFileName);
			//int dot = origFileName.lastIndexOf(".");
			//String ext = origFileName.substring(dot);
			String saveFileName = imageUploadPath + File.separator + uuid.toString().replace("-", "") + "." + extension; //저장파일명
			
			//이미지 저장
			out = new FileOutputStream(saveFileName);
			out.write(bytes);
			out.flush();
			
			//CKEditor로 전송
			printWriter = response.getWriter();
			String callback = request.getParameter("CKEditorFuncNum");
			String fileUrl = "/upload/product/content/" + uuid.toString().replace("-", "") + "." + extension; //main 하위의 저장 경로 이름 따라서 file url 설정
			
			printWriter.println("<script type='text/javascript'>"
					+ "window.parent.CKEDITOR.tools.callFunction("
					+ callback + ",'" + fileUrl + "',' 이미지를 업로드하였습니다.')"
					+"</script>");
			printWriter.flush();
			
			//DB 전송
			AttachmentDTO attachment = new AttachmentDTO();
			attachment.setOrigFileName(origFileName);
			int getLastSeparator = saveFileName.lastIndexOf("\\");
			attachment.setSaveFileName(saveFileName.substring(getLastSeparator));
			attachment.setSavePath(fileUrl);
			attachment.setThumbnailPath("NULL");
			attachment.setFileType("CONTENT");
			
			productService.attachProdContentImage(attachment);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) { out.close(); }
				if(printWriter != null) { printWriter.close(); }
			} catch(IOException e) { e.printStackTrace(); }
		}
	}
}
