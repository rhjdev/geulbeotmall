package com.reminder.geulbeotmall.member.model.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;

/**
 * Spring Security 모듈 UserDetailsService 상속 받아 로그인/로그아웃 로직 처리
 */
public interface MemberService extends UserDetailsService { 
	
	int checkId(String memberId);
	
	int checkEmail(String email);
	
	boolean signUpMember(MemberDTO member) throws Exception;
	
	void updateAccumLoginCount(String username);
	
	void updateLatestLoginDate(String username);
	
	void resetLoginFailedCount(String username);
	
	void updateLoginFailedCount(String username);

	int checkLoginFailedCount(String username);

	void deactivateUsername(String username);

	/* 마이페이지 */
	void addToWishList(String memberId, int optionNo);

	List<WishListDTO> getMemberWishList(String memberId);
	
	List<Integer> getProdNoFromWishList(String memberId);

	int deleteItemFromWishList(String memberId, int optionNo);

	List<OrderDetailDTO> getMemberOrderList(String memberId);

	List<PointDTO> getReserveDetails(String memberId);

	OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo);

	List<OrderDTO> getOptionListByOrderNo(String orderNo);

	int getTotalOrderAmountByOrderNo(String orderNo);

	List<OrderDTO> getItemsToPostAReview(String memberId);

	OrderDTO postAReview(String memberId, String orderNo, int optionNo);

	List<ReviewDTO> getMemberReviewPosts(String memberId);

	int getMemberPoint(String memberId);
}
