package com.reminder.geulbeotmall.admin.model.dto;

import java.util.Date;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import lombok.Data;

@Data
public class SuspDTO {
	
	private int suspNo;
	private MemberDTO memberId;
	private int accSuspCount;
	private String accSuspDesc;
	private Date accSuspDate;
}
