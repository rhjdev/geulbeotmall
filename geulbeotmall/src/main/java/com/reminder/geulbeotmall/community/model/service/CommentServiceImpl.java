package com.reminder.geulbeotmall.community.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.community.model.dao.CommentMapper;
import com.reminder.geulbeotmall.community.model.dto.CommentDTO;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

	private final CommentMapper commentMapper;
	
	@Autowired
	public CommentServiceImpl(CommentMapper commentMapper) {
		this.commentMapper = commentMapper;
	}

	@Override
	public List<CommentDTO> getCommentsInPost(String refBoard, int refPostNo) {
		return commentMapper.getCommentsInPost(refBoard, refPostNo);
	}
	
	@Override
	public List<CommentDTO> getNestedCommentsInPost(String refBoard, int refPostNo) {
		return commentMapper.getNestedCommentsInPost(refBoard, refPostNo);
	}

	@Override
	public int writeAComment(CommentDTO commentDTO) {
		return commentMapper.writeAComment(commentDTO);
	}
	
	@Override
	public int editAComment(int commentNo, String commentContent) {
		return commentMapper.editAComment(commentNo, commentContent);
	}

	@Override
	public int deleteAComment(int commentNo) {
		return commentMapper.deleteAComment(commentNo);
	}
}
