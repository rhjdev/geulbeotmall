package com.reminder.geulbeotmall.cart.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.cart.model.service.CartService;
import com.reminder.geulbeotmall.cart.model.service.OrderService;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.product.model.service.ProductService;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;

@Slf4j
@Controller
@SessionAttributes({"loginMember", "geulbeotCart", "orderItem"})
public class OrderController {
	
	private final OrderService orderService;
	private final CartService cartService;
	private final ProductService productService;
	
	@Autowired
	public OrderController(OrderService orderService, CartService cartService, ProductService productService) {
		this.orderService = orderService;
		this.cartService = cartService;
		this.productService = productService;
	}
	
	/**
	 * 주문결제페이지 호출
	 */
	@GetMapping("/cart/order")
	public void getOrderForm(@AuthenticationPrincipal UserImpl user, HttpServletRequest request, HttpSession session, Model model) {
		String optionNo = request.getParameter("optionNo"); //장바구니 단일상품
		String[] optionNoArr = request.getParameterValues("arr"); //장바구니 선택상품 또는 전체상품
		String optionNoFromWishList = request.getParameter("optionNoFromWishList"); //위시리스트 단일상품
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
		/* D. 위시리스트 단일상품주문 */
		if(optionNoFromWishList != null) {
			OptionDTO optionDTO = new OptionDTO();
			optionDTO = orderService.getWishItemByOptionNo(user.getMemberId(), Integer.parseInt(optionNoFromWishList));
			
			CartDTO cartDTO = new CartDTO(); //DB 저장용이 아닌 주문서 작성용 객체
			cartDTO.setMemberId(user.getMemberId());
			cartDTO.setProdNo(optionDTO.getRefProdNo());
			cartDTO.setOptionNo(optionDTO.getOptionNo());
			cartDTO.setQuantity(1);
			int prodNo = productService.searchProdNoByOptionNo(optionDTO.getOptionNo());
			cartDTO.setProduct(productService.getProductDetails(prodNo));
			cartDTO.setOption(optionDTO);
			cartDTO.setCategory(productService.getCategoryByProdNo(prodNo));
			cartDTO.setBrand(productService.getBrandByProdNo(prodNo));
			AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
			AttachmentDTO subThumb = productService.getSubThumbnailByProdNo(prodNo);
			List<AttachmentDTO> attachmentList = new ArrayList<>();
			attachmentList.add(mainThumb);
			attachmentList.add(subThumb);
			cartDTO.setAttachmentList(attachmentList);
			log.info("item from wishlist : {}", cartDTO);
			
			session.setAttribute("orderItem", cartDTO); //선택상품정보 출력용으로 session상에 임시 저장하여 전달
		}
		model.addAttribute("member", user);
	}
	
	/**
	 * 주문 및 결제
	 */
	@PostMapping(value="/cart/order", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String orderAndPay(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpSession session, Model model) {
		log.info("Server-side 주문 및 결제 시작");
		String result = "succeed";
		
		JSONArray optionNoArr = JSONArray.fromObject(params.get("optionNoArr"));
		JSONArray optionQtArr = JSONArray.fromObject(params.get("optionQtArr"));
		JSONArray orderPriceArr = JSONArray.fromObject(params.get("orderPriceArr"));
		
		String memberId = (String) params.get("memberId");
		log.info("memberId : {}", memberId);
		String rcvrName = params.get("rcvrName").toString();
		log.info("rcvrName : {}", rcvrName);
		String rcvrPhone = params.get("rcvrPhone").toString();
		String rcvrPostalCode = params.get("rcvrPostalCode").toString();
		String rcvrAddress = params.get("rcvrAddress").toString();
		String dlvrReqMessage = params.get("dlvrReqMessage").toString();
		String paymentNo = params.get("paymentNo").toString();
		String paymentMethod = params.get("paymentMethod").toString();
		String paymentAmount = params.get("paymentAmount").toString();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");
		String orderDate = simpleDateFormat.format(new Date());
		String paymentDate = orderDate;
		
		/* 1. 주문정보 DB 저장 및 재고수량 차감 반영 */
		String orderNo = "ORD" + paymentNo.substring(1);
		
		OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
		orderDetailDTO.setOrderNo(orderNo);
		orderDetailDTO.setMemberId(memberId);
		orderDetailDTO.setOrderDate(orderDate);
		orderDetailDTO.setRcvrName(rcvrName);
		orderDetailDTO.setRcvrPhone(rcvrPhone);
		orderDetailDTO.setRcvrAddress(rcvrPostalCode + " " + rcvrAddress);
		orderDetailDTO.setDlvrReqMessage(dlvrReqMessage);
		orderDetailDTO.setDlvrStatus("상품준비중");
		int addOrderDetail = orderService.addOrderDetail(orderDetailDTO);
		if(addOrderDetail == 1) {
			log.info("주문배송 정보 DB 저장 완료");
		} else {
			result = "failed";
		}
		
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderNo(orderNo);
		int count = 0;
		for(int i=0; i < optionNoArr.size(); i++) {
			int optionNo = Integer.parseInt(optionNoArr.getString(i));
			log.info("optionNo : {}", optionNo);
			int optionQt = Integer.parseInt(optionQtArr.getString(i));
			String orderAmount = orderPriceArr.getString(i);
			orderDTO.setOptionNo(optionNo);
			orderDTO.setOrderQuantity(optionQt);
			orderDTO.setOrderAmount(Integer.parseInt(orderAmount));
			int addOrderInfo = orderService.addOrderInfo(orderDTO);
			count += addOrderInfo;
		}
		if(count == optionNoArr.size()) {
			log.info("주문상품 정보 DB 저장 및 재고수량 차감 반영");
		} else {
			result = "failed";
		}
		
		/* 2. 결제정보 DB 저장 */
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPaymentNo(paymentNo);
		paymentDTO.setOrderNo(orderNo);
		paymentDTO.setPaymentMethod(paymentMethod);
		paymentDTO.setPaymentAmount(Integer.parseInt(paymentAmount));
		paymentDTO.setPaymentDateTime(paymentDate);
		int addPaymentInfo = orderService.addPaymentInfo(paymentDTO);
		if(addPaymentInfo == 1) {
			log.info("결제 정보 DB 저장 완료");
		} else {
			result = "failed";
		}
		log.info("result : {}", result);
		return result;
	}
}
