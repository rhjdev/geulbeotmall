package com.reminder.geulbeotmall.community.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.reminder.geulbeotmall.community.model.dto.CommentDTO;
import com.reminder.geulbeotmall.community.model.service.CommentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes("loginMember")
@RequestMapping("comment")
public class CommentController {

	private final CommentService commentService;
	private final MessageSource messageSource;
	
	@Autowired
	public CommentController(CommentService commentService, MessageSource messageSource) {
		this.commentService = commentService;
		this.messageSource = messageSource;
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
				redirectUrl = "redirect:/cs/inquiry/details?no=" + refPostNo;
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
