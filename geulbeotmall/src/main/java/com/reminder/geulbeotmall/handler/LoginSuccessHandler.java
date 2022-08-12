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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SessionAttributes("loginMember")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private MemberMapper memberMapper;
	private CartMapper cartMapper;
	
	@Autowired
	public LoginSuccessHandler(MemberMapper memberMapper, CartMapper cartMapper) {
		this.memberMapper = memberMapper;
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
		
		/* 3-2. 회원 권한 및 요청 상황에 따른 기본 페이지 이동 */
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		log.info("ROLE : {}", roles);
		
		if(roles.contains("ROLE_ADMIN")) { //관리자는 로그인 시 대시보드로 기본 이동
			response.sendRedirect("/admin/dashboard");
		} else if(roles.contains("ROLE_MEMBER") && uri != "") { //사이트 이용 중 요구된 로그인 인증을 마친 회원의 경우 이전 페이지로 이동
			response.sendRedirect(uri);
		} else {
			response.sendRedirect("/");
		}
	}
}
