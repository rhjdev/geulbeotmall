package com.reminder.geulbeotmall.cart.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.cart.model.service.CartService;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;

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
	public void getMyCart(Model model) {
		int countCartItems = cartMapper.countCartItems();
	}
	
	@PostMapping(value="/cart/mycart/add", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String addToCart(@RequestParam Map<String, Object> params, @ModelAttribute("loginMember") String loginMember, HttpServletRequest request, HttpSession session, Model model) {
		String[] optionNoArr = request.getParameterValues("orderOptionNo");
		String[] optionQtArr = request.getParameterValues("orderOptionQt");
		CartDTO cartDTO = new CartDTO();
		for(int i=0; i < optionNoArr.length; i++) {
			int checkCartItem = cartMapper.checkCartItem(Integer.parseInt(optionNoArr[i]));
			log.info("사용자가 선택한 옵션번호 : {}", optionNoArr[i]);
			
			if(checkCartItem == 0) { //장바구니에 이미 담겨있는 상품이 아닌 경우에만 추가
				if(loginMember == null) {
					List<CartDTO> nonememberCart = new ArrayList<>();
					nonememberCart.add(cartDTO);
					for(int j=0; j < nonememberCart.size(); j++) {
						nonememberCart.get(i).setCartNo(i);
					}
					session.setAttribute("geulbeotCart", nonememberCart);
					log.info("비회원용 session 장바구니 생성 : {}", nonememberCart);
					
				} else {
					log.info("요청한 사용자의 id : {}", loginMember);
					cartDTO.setMemberId(loginMember);
				}
				int prodNoByOptionNo = productMapper.searchProdNoByOptionNo(Integer.parseInt(optionNoArr[i]));
				cartDTO.setProdNo(prodNoByOptionNo);
				cartDTO.setOptionNo(Integer.parseInt(optionNoArr[i]));
				cartDTO.setQuantity(Integer.parseInt(optionQtArr[i]));
				cartMapper.addToCart(cartDTO);
			}
		}
		return "성공";
	}
	
	@GetMapping("/cart/mycart/get")
	public String getCart(@ModelAttribute("loginMember") UserImpl loginMember, CartDTO cartDTO, HttpSession session) {
		//회원용 장바구니
		//UserImpl loginMember = (UserImpl) session.getAttribute("loginMember"); //LoginSuccessHandler 통해 session에 저장해둔 객체를 꺼냄
		//String id = loginMember.getMemberId();
		//List<CartDTO> geulbeotCart = cartMapper.getMemberCart(id);
		
		//비회원용 장바구니
		if(session.getAttribute("geulbeotCart") == null) { //비회원용 session 장바구니가 존재하지 않는 경우 생성 먼저 진행
			List<CartDTO> nonememberCart = new ArrayList<>();
			nonememberCart.add(cartDTO);
			for(int i=0; i < nonememberCart.size(); i++) {
				nonememberCart.get(i).setCartNo(i);
			}
			session.setAttribute("geulbeotCart", nonememberCart);
			log.info("비회원용 session 장바구니 생성 : {}", nonememberCart);
			
			return "성공";
		} else { //비회원용 session 장바구니가 이미 존재하는 경우 불러오기
			List<CartDTO> nonememberCart = (List<CartDTO>) session.getAttribute("geulbeotCart");
			for(int i=0; i < nonememberCart.size(); i++) { //장바구니에 같은 상품이 담겼는지 점검한 뒤 추가
				if(nonememberCart.get(i).getOptionNo() == cartDTO.getOptionNo()) {
					return "실패";
				}
			}
			nonememberCart.add(cartDTO);
			for(int i=0; i < nonememberCart.size(); i++) {
				nonememberCart.get(i).setCartNo(i);
			}
			session.setAttribute("geulbeotCart", nonememberCart);
			return "성공";
		}
	}
}
