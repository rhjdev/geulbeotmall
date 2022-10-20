package com.reminder.geulbeotmall.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.cart.model.dao.CartMapper;
import com.reminder.geulbeotmall.cart.model.dto.CartDTO;
import com.reminder.geulbeotmall.member.model.dao.MemberMapper;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.product.model.dto.BrandDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;
import com.reminder.geulbeotmall.product.model.dto.ProductDTO;
import com.reminder.geulbeotmall.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SessionAttributes({"loginMember", "orderItem", "signInWithSocialAccount"})
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private MemberMapper memberMapper;
	private ProductMapper productMapper;
	private CartMapper cartMapper;
	
	@Autowired
	public LoginSuccessHandler(MemberMapper memberMapper, ProductMapper productMapper, CartMapper cartMapper) {
		this.memberMapper = memberMapper;
		this.productMapper = productMapper;
		this.cartMapper = cartMapper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("onAuthenticationSuccess");
		
		/* 1. 회원 정보 DB 업데이트 */
		UserImpl loginMember = (UserImpl) authentication.getPrincipal();
		String username = loginMember.getMemberId();
		
		request.getSession().setAttribute("loginMember", username); //session에 현재 로그인한 회원 정보 저장
		
		memberMapper.resetLoginFailedCount(username); //현재까지 기록된 로그인 실패 횟수 초기화
		memberMapper.updateAccumLoginCount(username); //누적 로그인 횟수 증가
		memberMapper.updateLatestLoginDate(username); //최근 로그인 일시 업데이트
		
		/* 2. 회원 장바구니 업데이트 및 불러오기 */
		HttpSession session = request.getSession();
		List<CartDTO> geulbeotCart = (List<CartDTO>) session.getAttribute("geulbeotCart");
		if(geulbeotCart == null) geulbeotCart = new ArrayList<>();
		log.info("로그인 전 session으로부터 담겨온 장바구니 size : {}", geulbeotCart.size());
		
		List<CartDTO> memberCart = cartMapper.getCurrentCart(username); //회원의 기존 장바구니 호출
		for(CartDTO c : memberCart) {
			log.info("기존 장바구니 : {}", c);
		}
		log.info("회원의 기존 장바구니 size : {}", memberCart.size());
		LoopA:
		for(int i=0; i < geulbeotCart.size(); i++) { //session 상품 목록을 DB에 추가
			CartDTO cartDTO = geulbeotCart.get(i);
			cartDTO.setMemberId(username);
			
			if(memberCart.size() == 0) {
				memberCart = new ArrayList<>();
				memberCart.add(cartDTO);
				cartMapper.addToCart(cartDTO);
				continue;
			}
			
			for(int j=0; j < memberCart.size(); j++) {
				if(geulbeotCart.get(i).getOptionNo() == memberCart.get(j).getOptionNo()) { //이미 담겨 있는 상품은 기존 목록에서 수량만 증가
					int quantity = memberCart.get(j).getQuantity() + geulbeotCart.get(i).getQuantity();
					cartDTO.setQuantity(quantity);
					memberCart.get(j).setQuantity(quantity);
					cartMapper.updateQuantityInCart(cartDTO.getMemberId(), cartDTO.getQuantity(), cartDTO.getOptionNo());
					continue LoopA;
				}
			}
			memberCart.add(cartDTO);
			cartMapper.addToCart(cartDTO);
		}
		session.setAttribute("geulbeotCart", memberCart);
		session.setAttribute("countCartItem", memberCart.size());
		
		/* 3-1. 로그인 상황별 이동 경로 저장 */
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response); //Spring Security가 요청을 가로채거든 URI 정보가 저장되는 객체
		
		String prevPage = (String) session.getAttribute("prevPage"); //MemberController로부터 저장돼 넘어온 사용자의 이전 경로
		if(prevPage != null) session.removeAttribute("prevPage");
		log.info("prevPage : {}", prevPage);
		
		String uri = request.getContextPath(); //uniform resource identifier
		if(savedRequest != null) { //상황A. 접근한 페이지가 Security에 의해 로그인 인증을 필요로 한 경우
			uri = savedRequest.getRedirectUrl();
			
		} else if(prevPage != null) { //상황B. 사용자가 직접 로그인페이지를 요청한 경우
			uri = prevPage;
			log.info("prevPage : {}", prevPage);
		}
		log.info("요청 uri : {}", uri);
		
		/*
		 * 비회원이 상품상세페이지에서 바로주문 요청 중 로그인하는 경우
		 * 1. 상품상세페이지의 JS 통해 선택정보를 로그인페이지로 전달
		 * 2. 로그인 Controller는 HttpServletRequest에 담겨온 파라미터 값을 꺼내 session상에 임시 저장
		 * 3. LoginSuccessHandler가 해당 session 객체를 토대로 상품정보를 조회하고, 이를 하나의 주문리스트로 생성해 주문페이지로 최종 연결
		 * => 로그인 전 선택한 상품정보 그대로 주문페이지로 이어지며, 이는 일회성으로 사용자의 기존 장바구니와는 무관함
		 */
		if(uri.indexOf("order") != -1) {
			session.removeAttribute("orderItem"); //주문 요청 시마다 주문목록 session 갱신
			
			String[] detailOptionNo = (String[]) session.getAttribute("detailOptionNo");
			String[] detailOptionQt = (String[]) session.getAttribute("detailOptionQt");
			if(detailOptionNo != null && detailOptionQt != null) {
				log.info("handler 확인 : {}", detailOptionNo.length);
				List<CartDTO> itemList = new ArrayList<>();
				for(int i=0; i < detailOptionNo.length; i++) {
					log.info("detailOptionNo : {}", detailOptionNo[i]);
					log.info("detailOptionQt : {}", detailOptionQt[i]);
					CartDTO cartDTO = new CartDTO();
					
					int prodNoByOptionNo = productMapper.searchProdNoByOptionNo(Integer.parseInt(detailOptionNo[i]));
					OptionDTO optionInfoByOptionNo = cartMapper.searchOptionInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
					ProductDTO productInfoByOptionNo = cartMapper.searchProductInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
					BrandDTO brandInfoByOptionNo = cartMapper.searchBrandInfoByOptionNo(Integer.parseInt(detailOptionNo[i]));
					AttachmentDTO mainThumb = productMapper.getMainThumbnailByProdNo(prodNoByOptionNo);
					AttachmentDTO subThumb = productMapper.getSubThumbnailByProdNo(prodNoByOptionNo);
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
				}
				session.setAttribute("orderItem", itemList); //선택상품정보 출력용으로 session상에 임시 저장하여 전달
			}
		}
		
		/* 3-2. 회원 권한 및 요청 상황에 따른 기본 페이지 이동 */
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		log.info("ROLE : {}", roles);
		/* 
		 * 우선순위A.임시 비밀번호로 로그인에 성공한 회원은 권한에 관계 없이 '회원정보수정 페이지'로 이동
		 * 우선순위B.사이트 이용 중 요구된 로그인 인증을 마친 회원의 경우 '이전 페이지'로 이동
		 * 우선순위C.관리자는 로그인 시 '대시보드'로 기본 이동
		 */
		char tempPwdYn = loginMember.getTempPwdYn();
		String targetUrl = "";
		String signInWithSocialAccount = (String) session.getAttribute("signInWithSocialAccount");
		log.info("social account : {}", signInWithSocialAccount);
		if(tempPwdYn == 'Y') {
			/* 
			 * 소셜로그인 관리A.
			 * 이메일로 발송된 임시 비밀번호 통해 '직접 로그인'하므로 일반로그인/소셜로그인 redirect response 구분 필요 없음
			 */
			log.info("tempPwdYn : {}", tempPwdYn);
			String resetPasswordRequired = "required";
			request.getRequestDispatcher("/member/signin?resetPasswordRequired=" + resetPasswordRequired).forward(request, response);
		} else {
			if(roles.contains("ROLE_ADMIN")) {
				response.sendRedirect("/admin/dashboard");
			/* 
			 * 소셜로그인 관리B.
			 * 일반로그인 계정은 sendRedirect() 사용 가능하지만, 소셜로그인 계정의 경우 setHeader 통해 redirect response 덮어쓰기 필요
			 * => 해결한 오류 : java.lang.IllegalStateException: Cannot call sendRedirect() after the response has been committed
			 */
			} else if(roles.contains("ROLE_MEMBER") && uri != "") {
				if(signInWithSocialAccount != null) {
					targetUrl = uri;
					response.setHeader("Location", targetUrl);
				} else {
					response.sendRedirect(uri);
				}
			} else {
				if(signInWithSocialAccount != null) {
					targetUrl = "/";
					response.setHeader("Location", targetUrl);
				} else {
					response.sendRedirect("/");
				}
			}
		}
	}
}
