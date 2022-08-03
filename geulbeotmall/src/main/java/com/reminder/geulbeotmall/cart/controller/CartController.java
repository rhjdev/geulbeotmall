package com.reminder.geulbeotmall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.cart.model.service.CartService;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"loginMember", "geulbeotCart"})
public class CartController {
	
	private final CartService cartService;
	private final CartMapper cartMapper;
	private final ProductMapper productMapper;
	
	@Autowired
	public CartController(CartService cartService, CartMapper cartMapper, ProductMapper productMapper) {
		this.cartService = cartService;
		this.cartMapper = cartMapper;
		this.productMapper = productMapper;
	}
	
	@GetMapping("/cart/mycart")
	public void getMyCart(Model model) {}
	
	@PostMapping(value="/cart/mycart/add", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String addToCart(HttpServletRequest request, HttpSession session, Model model) {
		String[] optionNoArr = request.getParameterValues("orderOptionNo");
		String[] optionQtArr = request.getParameterValues("orderOptionQt");
		
		String result = "";
		
		String loginMember = (String) session.getAttribute("loginMember");
		
		
		/* 비회원용 장바구니: session 장바구니가 존재하지 않는 경우 생성 먼저 진행 */
		if(loginMember == null && session.getAttribute("geulbeotCart") == null) {
			log.info("비회원용 session 장바구니 생성");
			List<CartDTO> nonmemberCart = new ArrayList<>();
			session.setAttribute("geulbeotCart", nonmemberCart);
		}
		
		LoopA:
		for(int i=0; i < optionNoArr.length; i++) {
			CartDTO cartDTO = new CartDTO(); //cart item
			
			int prodNoByOptionNo = productMapper.searchProdNoByOptionNo(Integer.parseInt(optionNoArr[i]));
			OptionDTO optionInfoByOptionNo = cartMapper.searchOptionInfoByOptionNo(Integer.parseInt(optionNoArr[i]));
			ProductDTO productInfoByOptionNo = cartMapper.searchProductInfoByOptionNo(Integer.parseInt(optionNoArr[i]));
			BrandDTO brandInfoByOptionNo = cartMapper.searchBrandInfoByOptionNo(Integer.parseInt(optionNoArr[i]));
			cartDTO.setProdNo(prodNoByOptionNo);
			cartDTO.setOptionNo(Integer.parseInt(optionNoArr[i]));
			cartDTO.setQuantity(Integer.parseInt(optionQtArr[i]));
			cartDTO.setOption(optionInfoByOptionNo);
			cartDTO.setProduct(productInfoByOptionNo);
			cartDTO.setBrand(brandInfoByOptionNo);
			log.info("cartDTO : {}", cartDTO);
			
			/* 비회원용 장바구니: session 장바구니 불러오기 */
			if(loginMember == null) {
				log.info("비회원용 장바구니 호출");
				List<CartDTO> nonmemberCart = (List<CartDTO>) session.getAttribute("geulbeotCart");
				log.info("현재 장바구니 size : {}", nonmemberCart.size());
				
				if(nonmemberCart.size() == 0) {
					nonmemberCart.add(cartDTO);
					continue;
				}
				
				for(int j=0; j < nonmemberCart.size(); j++) {
					log.info("nonmemberCart.get(j).getOptionNo() : {}", nonmemberCart.get(j).getOptionNo());
					log.info("Integer.parseInt(optionNoArr[i]) : {}", Integer.parseInt(optionNoArr[i]));
					if(nonmemberCart.get(j).getOptionNo() == Integer.parseInt(optionNoArr[i])) {
						result = "이미 장바구니에 담겨 있는 상품입니다"; //이미 장바구니에 담겨 있는 상품은 관련 메시지 출력
						continue LoopA; //다음 상품 옵션 번호로 옮겨감
					}
				}
				nonmemberCart.add(cartDTO);
				session.setAttribute("geulbeotCart", nonmemberCart);
				session.setAttribute("countCartItem", nonmemberCart.size());
				
				log.info("추가 후 장바구니 size : {}", nonmemberCart.size());
				
			/* 회원용 장바구니 */
			} else {
				log.info("요청한 사용자의 id : {}", loginMember);
				List<CartDTO> memberCart = cartMapper.getCurrentCart(loginMember); //DB에 저장된 회원의 기존 장바구니 호출
				
				int memberCartNo = cartMapper.getMemberCartNo(loginMember);
				cartDTO.setCartNo(memberCartNo);
				cartDTO.setMemberId(loginMember);
				if(memberCart == null) {
					memberCart = new ArrayList<>();
					memberCart.add(cartDTO);
					cartMapper.addToCart(cartDTO);
					continue;
				} else {
					for(int j=0; j < memberCart.size(); j++) {
						log.info("memberCart.get(j).getOptionNo() : {}", memberCart.get(j).getOptionNo());
						log.info("Integer.parseInt(optionNoArr[i]) : {}", Integer.parseInt(optionNoArr[i]));
						if(memberCart.get(j).getOptionNo() == Integer.parseInt(optionNoArr[i])) {
							int sum = memberCart.get(j).getQuantity() > 0? (memberCart.get(j).getQuantity() + cartDTO.getQuantity()) : cartDTO.getQuantity();
							memberCart.get(j).setQuantity(sum);
							cartDTO.setQuantity(sum);
							cartMapper.updateQuantityInCart(cartDTO);
							continue LoopA;
						}
					}
					memberCart.add(cartDTO);
					cartMapper.addToCart(cartDTO);
				}
				log.info("추가 후 회원 장바구니 size : {}", memberCart.size());
				
				session.setAttribute("geulbeotCart", memberCart);
				session.setAttribute("countCartItem", memberCart.size());
				model.addAttribute("memberCart", memberCart);
			}
		}
		
		log.info("최종 cart items : {}", session.getAttribute("geulbeotCart"));
		
		if(result.isEmpty()) {
			result = "성공";
		}
		return  result;
	}
}
