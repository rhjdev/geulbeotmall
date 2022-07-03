package com.reminder.geulbeotmall.paging.model.dto;

import lombok.Data;

@Data
public class PageDTO {
	
	private int pageSize;
	private int startPage;
	private int lastPage;
	private int latest;
	private boolean hasPreviousPage;
	private boolean hasNextPage;
	private int totalRecordCount;
	private Criteria criteria;
	
	public PageDTO() {}
	
	public PageDTO(int totalRecordCount, int pageSize, Criteria criteria) {
		this.totalRecordCount = totalRecordCount;
		this.pageSize = pageSize;
		this.criteria = criteria;
		
		this.lastPage = (int)(Math.ceil(criteria.getCurrentPageNo()*1.0 / pageSize))*pageSize;
		this.startPage = lastPage - (pageSize-1);
		
		this.latest = (int)(Math.ceil(totalRecordCount*1.0 / criteria.getRecordsPerPage()));
		
		if(lastPage > latest) {
			this.lastPage = latest == 0 ? 1 : latest;
		}
		
		this.hasPreviousPage = startPage > 1;
		this.hasNextPage = lastPage < latest;
	}
}
