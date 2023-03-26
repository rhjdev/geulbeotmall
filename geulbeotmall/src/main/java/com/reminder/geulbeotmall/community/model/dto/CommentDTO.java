package com.reminder.geulbeotmall.community.model.dto;

import java.util.Date;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

import lombok.Data;

@Data
public class CommentDTO {

	private int commentNo;
	private String memberId;
	private String refBoard; //게시판구분
	private int refPostNo; //게시글번호
	private String commentContent;
	private Date commentRegDate;
	private Date commentChgDate;
	private Date commentDelDate;
	private int commentNestLevel; //댓글계층구분
	private Integer commentNestedTo; //상단댓글정보(null 허용)
	private MemberDTO member;
}
