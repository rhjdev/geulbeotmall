package com.reminder.geulbeotmall.community.model.service;

import java.util.List;

import com.reminder.geulbeotmall.community.model.dto.CommentDTO;

public interface CommentService {

	List<CommentDTO> getCommentsInPost(String refBoard, int refPostNo);
	
	List<CommentDTO> getNestedCommentsInPost(String refBoard, int refPostNo);

	int writeAComment(CommentDTO commentDTO);
	
	int editAComment(int commentNo, String commentContent);

	int deleteAComment(int commentNo);

	int getTotalCommentNumber(String refBoard, int refPostNo);
}
