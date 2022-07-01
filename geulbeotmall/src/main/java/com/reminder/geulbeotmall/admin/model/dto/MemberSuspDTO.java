package com.reminder.geulbeotmall.admin.model.dto;

import com.reminder.geulbeotmall.member.model.dto.MemberDTO;

import lombok.Data;

@Data
public class MemberSuspDTO {
	
	private MemberDTO member;
	private SuspDTO susp;

}
