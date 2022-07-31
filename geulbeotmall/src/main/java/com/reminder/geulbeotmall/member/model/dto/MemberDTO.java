package com.reminder.geulbeotmall.member.model.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reminder.geulbeotmall.paging.model.dto.Criteria;

import lombok.Data;

@Data
public class MemberDTO {
	//@NotNull null(X), ""(O), " "(O)
	//@NotEmpty null(X), ""(X), " "(O)
	@NotBlank(message="{NotBlank.member.memberId}") //null, "", " " 모두 비허용
	@Pattern(regexp="/^[A-Za-z0-9]{6,15}$/", message="{Pattern.member.memberId}")
	private String memberId;		//회원ID
	
	@NotBlank
	@Pattern(regexp="/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$/")
	private String memberPwd;		//회원비밀번호
	
	@NotBlank
	private String name;			//이름
	
	@NotBlank
	private String phone;			//연락처
	
	@NotBlank
	@Email
	private String email;			//이메일
	
	private String address;			//주소
	private char agreement;			//마케팅정보수신동의여부
	
	private char accInactiveYn;		//계정비활성화여부(이메일 인증 전 기본값 Y)
	private char tempPwdYn;			//임시비밀번호여부(Y일 시 '비밀번호 재설정' 페이지로 이동)
	private int accumLoginCount;	//누적로그인횟수
	private int loginFailedCount;	//로그인연속실패횟수
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss SSS")
	private Date latestLoginDate;	//최근로그인일시
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss SSS")
	private Date accCreationDate;	//계정가입일자
	private Date accChangedDate;	//계정수정일자
	private Date accClosingDate;	//계정탈퇴일자
	private char accClosingYn;		//계정탈퇴여부
	
	private List<RoleDTO> roleList;
}
