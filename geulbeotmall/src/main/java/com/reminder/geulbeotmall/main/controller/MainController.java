package com.reminder.geulbeotmall.main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.main.model.service.MainService;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;
import com.reminder.geulbeotmall.upload.model.dto.DesignImageDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"loginMember", "geulbeotCart"})
public class MainController {
	
	private final MainService mainService;
	private final ProductService productService;
	private final MemberService memberService;
	
	@Autowired
	public MainController(MainService mainService, ProductService productService, MemberService memberService) {
		this.mainService = mainService;
		this.productService = productService;
		this.memberService = memberService;
	}

	@GetMapping(value={"/", "/main"})
	public String main(HttpSession session, Model model) {
		/* 슬라이더 */
		List<DesignImageDTO> slides = mainService.getSliderImages();
		/* 배너 */
		List<DesignImageDTO> banner = mainService.getBannerImage();
		
		/* 인기 상품 */
		List<ProductDTO> top8 = mainService.getTop8ProductByPopularity();
		List<ProductDTO> bestSelling = new ArrayList<>();
		List<AttachmentDTO> bestSellingThumbnailList = new ArrayList<>();
		Map<Integer, Integer> bestSellingReviewNumberMap = new HashMap<>();
		Map<Integer, Double> bestSellingReviewRatingMap = new HashMap<>();
		for(int i=0; i < top8.size(); i++) { //출력용 상품별 메인썸네일, 리뷰수, 평점
			int prodNo = top8.get(i).getProdNo();
			ProductDTO productDTO = productService.getProductDetails(prodNo);
			bestSelling.add(productDTO);
			AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
			bestSellingThumbnailList.add(mainThumb);
			int number = productService.getTotalNumberOfReviews(prodNo);
			bestSellingReviewNumberMap.put(prodNo, number);
			if(number > 0) {
				double averageRating = productService.averageReviewRating(prodNo);
				bestSellingReviewRatingMap.put(prodNo, averageRating);
			} else {
				bestSellingReviewRatingMap.put(prodNo, 0.0);
			}
		}
		
		/* 새 상품 */
		List<ProductDTO> latest8 = mainService.getLatest8ProductByEnrollDate();
		List<ProductDTO> newReleases = new ArrayList<>();
		List<AttachmentDTO> newReleasesThumbnailList = new ArrayList<>();
		Map<Integer, Integer> newReleasesReviewNumberMap = new HashMap<>();
		Map<Integer, Double> newReleasesReviewRatingMap = new HashMap<>();
		for(int i=0; i < latest8.size(); i++) { //출력용 상품별 메인썸네일, 리뷰수, 평점
			int prodNo = latest8.get(i).getProdNo();
			ProductDTO productDTO = productService.getProductDetails(prodNo);
			newReleases.add(productDTO);
			AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
			newReleasesThumbnailList.add(mainThumb);
			int number = productService.getTotalNumberOfReviews(prodNo);
			newReleasesReviewNumberMap.put(prodNo, number);
			if(number > 0) {
				double averageRating = productService.averageReviewRating(prodNo);
				newReleasesReviewRatingMap.put(prodNo, averageRating);
			} else {
				newReleasesReviewRatingMap.put(prodNo, 0.0);
			}
		}
		
		/* 회원의 경우 위시리스트 상품은 별도 표시 */
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember != null) {
			log.info("loginMember : {}", loginMember);
			List<Integer> memberWishItem = memberService.getProdNoFromWishList(loginMember);
			model.addAttribute("memberWishItem", memberWishItem);
		}
	
		model.addAttribute("slides", slides);
		model.addAttribute("banner", banner);
		model.addAttribute("bestSelling", bestSelling);
		model.addAttribute("bestSellingThumbnailList", bestSellingThumbnailList);
		model.addAttribute("bestSellingReviewNumberMap", bestSellingReviewNumberMap);
		model.addAttribute("bestSellingReviewRatingMap", bestSellingReviewRatingMap);
		model.addAttribute("newReleases", newReleases);
		model.addAttribute("newReleasesThumbnailList", newReleasesThumbnailList);
		model.addAttribute("newReleasesReviewNumberMap", newReleasesReviewNumberMap);
		model.addAttribute("newReleasesReviewRatingMap", newReleasesReviewRatingMap);
		return "main/main";
	}
	
	@PostMapping(value="/")
	public String redirectMain() {
		return "redirect:/";
	}
}
