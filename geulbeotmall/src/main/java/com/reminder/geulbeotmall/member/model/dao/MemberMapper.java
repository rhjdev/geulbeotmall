package com.reminder.geulbeotmall.member.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.RoleDTO;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;

@Mapper
public interface MemberMapper {
	
	/* 회원가입 */
	int checkId(String memberId);				//아이디 중복 검사
	
	int checkEmail(String email);				//이메일 중복 검사
	
	int insertMember(MemberDTO member);			//회원 정보 등록

	int insertRole(RoleDTO role);				//회원 권한 등록
	
	int insertAuthentication(String memberId);	//회원 인증 등록
	
	/* 로그인 */
	void updateAccumLoginCount(String username);	//성공 시 로그인 횟수 누적
	
	void updateLatestLoginDate(String username);	//성공 시 최근 로그인 일시 업데이트
	
	void resetLoginFailedCount(String username);	//성공 시 로그인 실패 횟수 리셋
	
	void updateLoginFailedCount(String username);	//실패 시 로그인 실패 횟수 증가
	
	int checkLoginFailedCount(String username);		//로그인 실패 횟수 조회
	
	MemberDTO findMemberById(String username);

	void deactivateUsername(String username);
	
	/* 마이페이지 */
	void addToWishList(String memberId, int optionNo);

	List<WishListDTO> getMemberWishList(String memberId);
	
	List<Integer> getProdNoFromWishList(String memberId);

	int deleteItemFromWishList(String memberId, int optionNo);
	
	List<OrderDetailDTO> getMemberOrderList(String memberId);
	
	OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo);

	List<PointDTO> getReserveDetails(String memberId);

	List<OrderDTO> getOptionListByOrderNo(String orderNo);

	int getTotalOrderAmountByOrderNo(String orderNo);

	List<OrderDTO> getItemsToPostAReview(String memberId);

	OrderDTO postAReview(String memberId, String orderNo, int optionNo);

	List<ReviewDTO> getMemberReviewPosts(String memberId);

	int getMemberPoint(String memberId);

	MemberDTO getMemberDetails(String memberId);

	char checkIsAuthenticated(String memberId);

	int updateAuthentication(String memberId, char authPhoneYn);

	int changeMemberInfo(MemberDTO memberDTO);
}
