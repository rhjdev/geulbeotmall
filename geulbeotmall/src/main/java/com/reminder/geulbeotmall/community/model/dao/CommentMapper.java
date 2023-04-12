package com.reminder.geulbeotmall.community.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.community.model.dto.CommentDTO;

@Mapper
public interface CommentMapper {

	List<CommentDTO> getCommentsInPost(String refBoard, int refPostNo);
	
	List<CommentDTO> getNestedCommentsInPost(String refBoard, int refPostNo);

	int writeAComment(CommentDTO commentDTO);
	
	int editAComment(int commentNo, String commentContent);

	int deleteAComment(int commentNo);

	int getTotalCommentNumber(String refBoard, int refPostNo);
}
