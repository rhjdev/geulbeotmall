package com.reminder.geulbeotmall.product.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.reminder.geulbeotmall.product.model.dto.CategoryDTO;
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
	
	@PostMapping(value="/admin/product/addOption", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView addProductOptionAndStock(@RequestBody Map<String, String> params, Locale local) {
		
		log.info("옵션 추가 시작");
		
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		String bodyColor = params.get("bodyColor");
		String inkColor = params.get("inkColor");
		double pointSize = Double.parseDouble(params.get("pointSize"));
		int stockAmount = Integer.parseInt(params.get("stockAmount"));
		int extraCharge = Integer.parseInt(params.get("extraCharge"));
		
		int stockResult = productService.addProductStock(stockAmount);
		int optionResult = productService.addProductOption(bodyColor, inkColor, pointSize, extraCharge);
		
		if(stockResult == 1 && optionResult == 1) {
			log.info("옵션 추가 완료");
		} else {
			mv.addObject("errorMessage", messageSource.getMessage("errorWhileAddingAnOption", null, local));
		}
		
		return mv;
	}
	@PostMapping(value="/admin/product/add", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView addProduct(@RequestBody Map<String, String> params, Locale locale) {
		return null;
	}
}
