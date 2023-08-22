package com.reminder.geulbeotmall.community.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.community.model.dto.CommentDTO;
import com.reminder.geulbeotmall.community.model.service.CommentService;
import com.reminder.geulbeotmall.cs.model.service.CSService;
import com.reminder.geulbeotmall.member.model.service.MemberService;
import com.reminder.geulbeotmall.notification.model.dto.Notification;
import com.reminder.geulbeotmall.notification.model.dto.NotificationType;
import com.reminder.geulbeotmall.notification.model.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes("loginMember")
@RequestMapping("comment")
public class CommentController {

	private final CommentService commentService;
	private final MemberService memberService;
	private final CSService csService;
	private final NotificationService notificationService;
	
	@Autowired
	public CommentController(CommentService commentService, MemberService memberService, CSService csService, NotificationService notificationService) {
		this.commentService = commentService;
		this.memberService = memberService;
		this.csService = csService;
		this.notificationService = notificationService;
	}
	
	/* 댓글/대댓글 작성 */
	@PostMapping("write")
	public String writeAComment(@ModelAttribute CommentDTO commentDTO, HttpSession session) {
		log.info("write a new comment : {}", commentDTO);
		commentDTO.setMemberId((String) session.getAttribute("loginMember"));
		String refBoard = commentDTO.getRefBoard();
		int refPostNo = commentDTO.getRefPostNo();
		
		int result = commentService.writeAComment(commentDTO);
		
		String redirectUrl = "";
		if(result == 1) {
			/* 댓글 허용하는 게시판별 새로고침 설정 */
			if(refBoard.equals("문의")) {
				char isWrittenByAdmin = memberService.checkAdminOrNot((String) session.getAttribute("loginMember")) == 1 ? 'Y' : 'N'; //관리자 확인
				if(isWrittenByAdmin == 'Y') csService.updateInquiryAnsweredYn(refPostNo); //답변상태 '완료' 반영
				redirectUrl = "redirect:/cs/inquiry/details?no=" + refPostNo;
				
				/* SSE 통신 및 DB 저장(댓글 및 대댓글 등록 알림)
				 * => [제목...]게시글에 댓글이 달렸습니다: [댓글...]
				 * => [댓글...]댓글에 답댓글이 달렸습니다: [대댓글...]
				 */
				String notificationId = "";
				String receiver = "";
				String title = "";
				String content = "";
				String type = "";
				if(commentDTO.getCommentNestLevel() == 1) { //댓글(nest level 1)
					Map<String, String> inquiryTitleAndWriter = commentService.checkInquiryPostWriter(refPostNo);
					receiver = inquiryTitleAndWriter.get("MEMBER_ID"); //게시글 작성자
					notificationId = receiver + "_" + System.currentTimeMillis();
					title = inquiryTitleAndWriter.get("INQUIRY_TITLE");
					type = NotificationType.COMMENT.getAlias();
					content = "["
							+ title.substring(0, 3) + "..."
							+ "]"
							+ "게시글에 댓글이 달렸습니다: "
							+ "["
							+ commentDTO.getCommentContent().substring(0, 3) + "..."
							+ "]";
				} else { //대댓글(nest level 2)
					CommentDTO parentComment = commentService.checkParentComment(commentDTO.getCommentNestedTo());
					receiver = parentComment.getMemberId(); //댓글 작성자
					notificationId = receiver + "_" + System.currentTimeMillis();
					type = NotificationType.REPLY.getAlias();
					content = "["
							+ parentComment.getCommentContent().substring(0, 3) + "..."
							+ "]"
							+ "댓글에 답댓글이 달렸습니다: "
							+ "["
							+ commentDTO.getCommentContent().substring(0, 3) + "..."
							+ "]";
				}
				
				Notification notification = Notification.builder()
						.notificationId(notificationId)
						.receiver(receiver)
						.content(content)
						.notificationType(type)
						.url("/cs/inquiry/details?no=" + refPostNo)
						.readYn('N')
						.deletedYn('N')
						.build();
				notificationService.sendNotification(notification);
				log.info("comment/nested comment notification added : {}", notification);
			}
		}
		return redirectUrl;
	}
	
	/* 댓글/대댓글 수정 */
	@PostMapping("edit")
	public String editAComment(@ModelAttribute CommentDTO commentDTO, HttpSession session) {
		log.info("edit a new comment : {}", commentDTO);
		int commentNo = commentDTO.getCommentNo();
		String commentContent = commentDTO.getCommentContent();
		
		int result = commentService.editAComment(commentNo, commentContent);
		
		String refBoard = commentDTO.getRefBoard();
		int refPostNo = commentDTO.getRefPostNo();
		
		String redirectUrl = "";
		if(result == 1) {
			/* 댓글 허용하는 게시판별 새로고침 설정 */
			if(refBoard.equals("문의")) {
				redirectUrl = "redirect:/cs/inquiry/details?no=" + refPostNo;
			}
		}
		return redirectUrl;
	}
	
	/* 댓글 삭제 */
	@GetMapping("delete")
	@ResponseBody //ajax 통신
	public void deleteAComment(int commentNo) {
		log.info("delete comment no : {}", commentNo);
		
		int result = commentService.deleteAComment(commentNo);
		if(result == 1) log.info("comment deleted");
	}
}
