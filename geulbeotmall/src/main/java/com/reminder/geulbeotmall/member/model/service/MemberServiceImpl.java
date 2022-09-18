package com.reminder.geulbeotmall.member.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.member.model.dao.MemberMapper;
import com.reminder.geulbeotmall.member.model.dto.AuthorityDTO;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.member.model.dto.RoleDTO;
import com.reminder.geulbeotmall.member.model.dto.UserImpl;
import com.reminder.geulbeotmall.member.model.dto.WishListDTO;
import com.reminder.geulbeotmall.review.model.dto.ReviewDTO;

@Transactional
@Service("memberService")
public class MemberServiceImpl implements MemberService {
	
	private MemberMapper memberMapper;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
		this.memberMapper = memberMapper;
		this.passwordEncoder = passwordEncoder;
		/* [PasswordEncoder]
		 * 1. pom.xml 통해 spring-boot-starter-security 의존성 주입
		 * 2. ContextConfiguration에서 BCryptPasswordEncoder에 대해 Bean 등록
		 */
	}
	
	/**
	 * 아이디 중복 검사
	 * @return 중복된 아이디 개수
	 */
	@Transactional(readOnly = true)
	@Override
	public int checkId(String memberId) {
		int count = memberMapper.checkId(memberId);
		return count;
	}
	
	/** 
	 * 이메일 중복 검사
	 * @return 중복된 이메일 개수
	 */
	@Transactional(readOnly = true)
	@Override
	public int checkEmail(String email) {
		int count = memberMapper.checkEmail(email);
		return count;
	}
	
	/**
	 * 회원가입
	 */
	@Override
	public boolean signUpMember(MemberDTO member) throws Exception {
		
		member.setMemberPwd(passwordEncoder.encode(member.getMemberPwd()));
		
		/* 1. 회원 등록 */
		int resultA = memberMapper.insertMember(member);
		
		/* 2. 권한 등록 */
		RoleDTO role = new RoleDTO();
		role.setMemberId(member.getMemberId());
		role.setAuthorityCode(1); //일반회원
		int resultB = memberMapper.insertRole(role);
		
		/* 3. 인증여부 등록(기본 N) */
		int resultC = 0;
		if(resultA <= 0 && resultB <= 0) {
			throw new Exception("신규 회원 등록에 실패");
		} else {
			resultC = memberMapper.insertAuthentication(member.getMemberId());
		}
		
		return resultA > 0 && resultB > 0 && resultC > 0 ? true : false;
	}

	/**
	 * 로그인
	 * UserImpl 통해 id, pwd, authorities 이외에 추가 정보 호출
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		MemberDTO member = memberMapper.findMemberById(username); //회원 조회
		
		if(member == null) member = new MemberDTO();			  //null인 경우 빈 객체 반환하여 대체
		
		List<GrantedAuthority> authorities = new ArrayList<>();	  //권한 리스트
		
		if(member != null && member.getRoleList() != null) {
			
			for(RoleDTO role : member.getRoleList()) {
				AuthorityDTO authority = role.getAuthority();
				
				if(authority != null) {
					authorities.add(new SimpleGrantedAuthority(authority.getAuthName())); //권한이름 전달
				}
			}
		}
		
		UserImpl user = new UserImpl(member.getMemberId(), member.getMemberPwd(), authorities);
		user.setDetails(member);
		
		return user;
	}
	
	@Override
	public void updateAccumLoginCount(String username) {
		memberMapper.updateAccumLoginCount(username);
	}
	
	@Override
	public void updateLatestLoginDate(String username) {
		memberMapper.updateLatestLoginDate(username);
	}

	@Override
	public void resetLoginFailedCount(String username) {
		memberMapper.resetLoginFailedCount(username);
	}

	@Override
	public void updateLoginFailedCount(String username) {
		memberMapper.updateLoginFailedCount(username);
	}

	@Override
	public int checkLoginFailedCount(String username) {
		return memberMapper.checkLoginFailedCount(username);
	}

	@Override
	public void deactivateUsername(String username) {
		memberMapper.deactivateUsername(username);
	}

	@Override
	public void addToWishList(String memberId, int optionNo) {
		memberMapper.addToWishList(memberId, optionNo);
	}

	@Override
	public List<WishListDTO> getMemberWishList(String memberId) {
		return memberMapper.getMemberWishList(memberId);
	}

	@Override
	public List<Integer> getProdNoFromWishList(String memberId) {
		return memberMapper.getProdNoFromWishList(memberId);
	}
	
	@Override
	public int deleteItemFromWishList(String memberId, int optionNo) {
		return memberMapper.deleteItemFromWishList(memberId, optionNo);
	}

	@Override
	public List<OrderDetailDTO> getMemberOrderList(String memberId) {
		return memberMapper.getMemberOrderList(memberId);
	}
	
	@Override
	public OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo) {
		return memberMapper.getMemberOrderDetails(memberId, orderNo);
	}

	@Override
	public List<PointDTO> getReserveDetails(String memberId) {
		return memberMapper.getReserveDetails(memberId);
	}

	@Override
	public List<OrderDTO> getOptionListByOrderNo(String orderNo) {
		return memberMapper.getOptionListByOrderNo(orderNo);
	}

	@Override
	public int getTotalOrderAmountByOrderNo(String orderNo) {
		return memberMapper.getTotalOrderAmountByOrderNo(orderNo);
	}

	@Override
	public List<OrderDTO> getItemsToPostAReview(String memberId) {
		return memberMapper.getItemsToPostAReview(memberId);
	}

	@Override
	public OrderDTO postAReview(String memberId, String orderNo, int optionNo) {
		return memberMapper.postAReview(memberId, orderNo, optionNo);
	}

	@Override
	public List<ReviewDTO> getMemberReviewPosts(String memberId) {
		return memberMapper.getMemberReviewPosts(memberId);
	}

	@Override
	public int getMemberPoint(String memberId) {
		return memberMapper.getMemberPoint(memberId);
	}

	@Override
	public MemberDTO getMemberDetails(String memberId) {
		return memberMapper.getMemberDetails(memberId);
	}

	@Override
	public char checkIsAuthenticated(String memberId) {
		return memberMapper.checkIsAuthenticated(memberId);
	}

	@Override
	public boolean updateAuthentication(String memberId, char authPhoneYn) {
		int result = memberMapper.updateAuthentication(memberId, authPhoneYn);
		return result == 1 ? true : false;
	}

	@Override
	public int changeMemberInfo(MemberDTO memberDTO) {
		return memberMapper.changeMemberInfo(memberDTO);
	}
}
