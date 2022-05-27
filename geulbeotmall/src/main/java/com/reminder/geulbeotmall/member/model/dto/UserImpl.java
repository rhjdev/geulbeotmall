package com.reminder.geulbeotmall.member.model.dto;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;

/* 메소드 통해 값을 꺼내거나 출력해서 확인할 용도로 Getter, ToString 선언 */
@Getter
@ToString
public class UserImpl extends User {
	
	/**
	 * MemberDTO 타입의 필드
	 */
	private String memberId;		//회원ID
	private String memberPwd;		//회원비밀번호
	private String name;			//이름
	private String phone;			//연락처
	private String email;			//이메일
	private String address;			//주소
	private char agreement;			//마케팅정보수신동의여부
	
	private char accInactiveYn;		//계정비활성화여부(이메일 인증 전 기본값 Y)
	private char tempPwdYn;			//임시비밀번호여부(Y일 시 '비밀번호 재설정' 페이지로 이동)
	private int accumLoginCount;	//누적로그인횟수
	private int loginFailedCount;	//로그인연속실패횟수
	private Date latestLoginDate;	//최근로그인일시
	private Date accCreationDate;	//계정가입일자
	private Date accChangedDate;	//계정수정일자
	private Date accClosingDate;	//계정탈퇴일자
	private char accClosingYn;		//계정탈퇴여부
	
	private List<RoleDTO> roleList;
	
	/**
	 * User로부터 상속 받은 생성자 
	 */
	public UserImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	/**
	 * 상세 데이터 호출용 메소드
	 */
	public void setDetails(MemberDTO member) {
		this.memberId = member.getMemberId();
		this.memberPwd = member.getMemberPwd();
		this.name = member.getName();
		this.phone = member.getPhone();
		this.email = member.getEmail();
		this.address = member.getAddress();
		this.agreement = member.getAgreement();
		this.accInactiveYn = member.getAccInactiveYn();
		this.tempPwdYn = member.getTempPwdYn();
		this.accumLoginCount = member.getAccumLoginCount();
		this.loginFailedCount = member.getLoginFailedCount();
		this.latestLoginDate = member.getLatestLoginDate();
		this.accCreationDate = member.getAccCreationDate();
		this.accChangedDate = member.getAccChangedDate();
		this.accClosingDate = member.getAccClosingDate();
		this.accClosingYn = member.getAccClosingYn();
	}
	
}
