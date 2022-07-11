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

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.StockDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;

import lombok.extern.slf4j.Slf4j;

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
	 * @return 상품등록 페이지 이동
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
	 * @return 카테고리 중복 검사 및 새 카테고리 추가 결과
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
	 * @return 브랜드 중복 검사 및 새 브랜드 추가 결과
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
	 * 상품 등록
	 */
	@Value("${image.upload.path}")
	private String uploadPath;
	@Value("${resource.handler}")
	private String resourceHandler;
	@PostMapping(value="/admin/product/add", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView addProduct(@RequestBody Map<String, Object> params, MultipartFile image, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		log.info("상품 추가 시작");
		
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
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
			log.info(stockAmounts[i]);
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
			
			productService.addProductOption(bodyColor, inkColor, pointSize, extraCharge);
		}
		
		if(result == 1) {
			mv.addObject("errorMessage", "성공");
		} else {
			mv.addObject("errorMessage", "오류 발생");
		}
		
		return mv;
	}
	
	/**
	 * 
	 */
	@PostMapping("/admin/product/add/uploadProductDetailImage")
	public void uploadProductDetailImage(MultipartFile upload, HttpServletRequest request, HttpServletResponse response) {
//		
//		String fileUploadDirectory = "/resources/static/upload/original/";
//		String thumbnailDirectory = "/resources/static/upload/thumbnail/";
//		
//		File directory1 = new File(fileUploadDirectory);
//		File directory2 = new File(thumbnailDirectory);
//		
//		/* 파일 저장경로가 존재하지 않는 경우 디렉토리를 생성 */
//		if(!directory1.exists() || !directory2.exists()) {
//			log.info("폴더 생성 : {}", directory1.mkdirs()); //폴더를 한 개만 생성할 거면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
//			log.info("폴더 생성 : {}", directory2.mkdirs());
//		}
//		
//		String origFileName = image.getOriginalFilename(); //원본파일명
//		
//		String extension = FilenameUtils.getExtension(origFileName);
//		//int dot = origFileName.lastIndexOf(".");
//		//String ext = origFileName.substring(dot);
//		String saveFileName = UUID.randomUUID().toString().replace("-", "") + extension; //저장파일명
//		
//		String savePath = fileUploadDirectory;
//		
//		/* parsing 된 request 파일을 저장한 뒤
//		 * 파일에 대한 정보는 List에, 다른 파라미터 정보는 모두 Map에 담김 */
//		Map<String, String> param = new HashMap<>();
//		List<Map<String, String>> fileList = new ArrayList<>();
//		
//		int maxFileSize = 1024 * 1024 * 10;
//		
//		/* 파일을 업로드할 때 최대 크기, 임시 저장할 폴더의 경로 등을 포함하기 위한 인스턴스 */
//		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
//		fileItemFactory.setRepository(new File(fileUploadDirectory));
//		fileItemFactory.setSizeThreshold(maxFileSize);
//		
//		ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
//		
//		
//		String mainThumb = params.get("mainThumb");
//		String subThumb = params.get("subThumb");
//		
//		ProductDTO product = new ProductDTO();
//		product.setAttachmentList(new ArrayList<AttachmentDTO>());
//		List<AttachmentDTO> attachList = product.getAttachmentList();
//		for(int i=0; i < fileList.size(); i++) {
//			Map<String, String> file = fileList.get(i);
//			
//			AttachmentDTO tempFileInfo = new AttachmentDTO();
//			tempFileInfo.setOrigFileName(file.get("origFileName"));
//			tempFileInfo.setSaveFileName(file.get("saveFileName"));
//			tempFileInfo.setSavePath(file.get("savePath"));
//			tempFileInfo.setThumbnailPath(file.get("thumbnailPath"));
//			
//			attachList.add(tempFileInfo);
//		}
//		
//		String bodyColor = params.get("bodyColor");
//		String inkColor = params.get("inkColor");
//		double pointSize = Double.parseDouble(params.get("pointSize"));
//		int stockAmount = Integer.parseInt(params.get("stockAmount"));
//		int extraCharge = Integer.parseInt(params.get("extraCharge"));
//		
//		int stockResult = productService.addProductStock(stockAmount);
//		int optionResult = productService.addProductOption(bodyColor, inkColor, pointSize, extraCharge);
//		
//		if(stockResult == 1 && optionResult == 1) {
//			log.info("옵션별 입고 추가 완료");
//		} else {
//			mv.addObject("errorMessage", messageSource.getMessage("errorWhileAddingAnOption", null, locale));
//		}
//		
		
		OutputStream out = null; //지정된 공간에 이미지 파일들을 내보내기하여 저장
		PrintWriter printWriter = null; //View에 띄워줄 구문 작성
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String fileUploadDirectory = "/resources/upload/original/";
		String thumbnailDirectory = "/resources/upload/thumbnail/";
		
		File directory1 = new File(fileUploadDirectory);
		File directory2 = new File(thumbnailDirectory);
		
		/* 파일 저장경로가 존재하지 않는 경우 디렉토리를 생성 */
		if(!directory1.exists() || !directory2.exists()) {
			log.info("폴더 생성 : {}", directory1.mkdirs()); //폴더를 한 개만 생성할 거면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
			log.info("폴더 생성 : {}", directory2.mkdirs());
		}
		
		/* parsing 된 request 파일을 저장한 뒤
		 * 파일에 대한 정보는 List에, 다른 파라미터 정보는 모두 Map에 담김 */
		Map<String, String> param = new HashMap<>();
		List<Map<String, String>> fileList = new ArrayList<>();
		
		int maxFileSize = 1024 * 1024 * 10;
		
		/* 파일을 업로드할 때 최대 크기, 임시 저장할 폴더의 경로 등을 포함하기 위한 인스턴스 */
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(new File(fileUploadDirectory));
		fileItemFactory.setSizeThreshold(maxFileSize);
		
		
		UUID uuid = UUID.randomUUID();
		String extension = FilenameUtils.getExtension(upload.getOriginalFilename()); //commons-io dependency 추가
		
		try {
			
			byte[] bytes = upload.getBytes();
			
			//실제 이미지 저장 경로
			String imageUploadPath = uploadPath + File.separator + uuid + "." + extension;
			
			//이미지 저장
			out = new FileOutputStream(imageUploadPath);
			out.write(bytes);
			out.flush();
			
			//CKEditor로 전송
			printWriter = response.getWriter();
			String callback = request.getParameter("CKEditorFuncNum");
			String fileUrl = "/admin/product/add/image/" + uuid + "." + extension;
			
			printWriter.println("<script type='text/javascript'>"
					+ "window.parent.CKEDITOR.tools.callFunction("
					+ callback + ",'" + fileUrl + "',' 이미지를 업로드하였습니다.')"
					+"</script>");
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) { out.close(); }
				if(printWriter != null) { printWriter.close(); }
			} catch(IOException e) { e.printStackTrace(); }
		}
	}
}
