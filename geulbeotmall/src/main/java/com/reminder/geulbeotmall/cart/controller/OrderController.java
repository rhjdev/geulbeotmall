package com.reminder.geulbeotmall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.service.CartService;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/cart/order")
@SessionAttributes({"loginMember", "geulbeotCart", "orderItem"})
public class OrderController {
	
	private final CartService cartService;
	private final ProductService productService;
	
	@Autowired
	public OrderController(CartService cartService, ProductService productService) {
		this.cartService = cartService;
		this.productService = productService;
	}

	@GetMapping("")
	public void getOrderForm(@AuthenticationPrincipal UserImpl user, HttpServletRequest request, HttpSession session, Model model) {
		String optionNo = request.getParameter("optionNo"); //장바구니 단일상품
		String[] optionNoArr = request.getParameterValues("arr"); //장바구니 선택상품 또는 전체상품
		String[] detailOptionNo = request.getParameterValues("orderOptionNo");
		String[] detailOptionQt = request.getParameterValues("orderOptionQt");
		List<CartDTO> memberCart = cartService.getCurrentCart(user.getMemberId());
		
		/* A. 상품상세페이지 바로주문 */
		if(detailOptionNo != null && detailOptionQt != null) {
			List<CartDTO> itemList = new ArrayList<>();
			for(int i=0; i < detailOptionNo.length; i++) {
				log.info("detailOptionNo : {}", detailOptionNo[i]);
				log.info("detailOptionQt : {}", detailOptionQt[i]);
				CartDTO cartDTO = new CartDTO();
				
				int prodNoByOptionNo = productService.searchProdNoByOptionNo(Integer.parseInt(detailOptionNo[i]));
				OptionDTO optionInfoByOptionNo = cartService.searchOptionInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
				ProductDTO productInfoByOptionNo = cartService.searchProductInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
				BrandDTO brandInfoByOptionNo = cartService.searchBrandInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
				AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNoByOptionNo);
				AttachmentDTO subThumb = productService.getSubThumbnailByProdNo(prodNoByOptionNo);
				List<AttachmentDTO> attachmentList = new ArrayList<>();
				attachmentList.add(mainThumb);
				attachmentList.add(subThumb);
				cartDTO.setProdNo(prodNoByOptionNo);
				cartDTO.setOptionNo(Integer.parseInt(detailOptionNo[i]));
				cartDTO.setQuantity(Integer.parseInt(detailOptionQt[i]));
				cartDTO.setOption(optionInfoByOptionNo);
				cartDTO.setProduct(productInfoByOptionNo);
				cartDTO.setBrand(brandInfoByOptionNo);
				cartDTO.setAttachmentList(attachmentList);
				log.info("cartDTO : {}", cartDTO);
				itemList.add(cartDTO);
				
				session.setAttribute("optionNoToOrder", detailOptionNo);
				session.setAttribute("optionQtToOrder", detailOptionQt);
			}
			session.setAttribute("orderItem", itemList); //선택상품정보 출력용으로 session상에 임시 저장하여 전달
		}
		/* B. 장바구니 단일상품주문 */
		if(optionNoArr == null && optionNo != null) {
			CartDTO cartDTO = new CartDTO();
			for(int i=0; i < memberCart.size(); i++) {
				if(memberCart.get(i).getOptionNo() == Integer.parseInt(optionNo)) {
					cartDTO = cartService.getCartItemByOptionNo(user.getMemberId(), Integer.parseInt(optionNo));
					log.info("item : {}", cartDTO);
				} else {
					continue;
				}
			}
			session.setAttribute("orderItem", cartDTO); //선택상품정보 출력용으로 session상에 임시 저장하여 전달
		/* C. 장바구니 선택 또는 전체상품주문 */
		} else if(optionNoArr != null && optionNo == null) {
			List<CartDTO> itemList = new ArrayList<>();
			for(int i=0; i < memberCart.size(); i++) {
				for(int j=0; j < optionNoArr.length; j++) {
					CartDTO cartDTO = new CartDTO();
					if(memberCart.get(i).getOptionNo() == Integer.parseInt(optionNoArr[j])) {
						cartDTO = cartService.getCartItemByOptionNo(user.getMemberId(), Integer.parseInt(optionNoArr[j]));
						log.info("item : {}", cartDTO);
						itemList.add(cartDTO);
					} else {
						continue;
					}
				}
			}
			session.setAttribute("orderItem", itemList); //선택상품정보 출력용으로 session상에 임시 저장하여 전달
		}
		model.addAttribute("member", user);
	}
	
	/**
	 * 장바구니 단일 상품 주문
	 */
	@PostMapping("/item")
	@ResponseBody
	public String orderItemInCart(@RequestParam("optionNo") String optionNo, HttpSession session, Model model) {
		String loginMember = (String) session.getAttribute("loginMember");
		
		OrderDetailDTO singleItemOrder = new OrderDetailDTO();
		singleItemOrder.setMemberId(loginMember);
		//log.info("item : {}", productDTO);
		
		String result = "";
		
		//model.addAttribute("orderList", productDTO);
		return result;
	}
	
	@PostMapping("/items")
	@ResponseBody
	public void orderItemsInCart(HttpSession session, Model model) {
//		String[] optionNoArr = request.getParameterValues("orderOptionNo");
//		String[] optionQtArr = request.getParameterValues("orderOptionQt");
		
		/* 주문 전 로그인 여부 확인 */
		//if(session.getAttribute("loginMember") == null) result = "로그인";
		
		String loginMember = (String) session.getAttribute("loginMember");
		
	}
}
