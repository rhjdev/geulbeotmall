package com.reminder.geulbeotmall.paging.model.dto;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;

@Data
public class ItemCriteria {

	private int page;
	private int items; //per page
	private String section;
	private String sortBy;
	
	public ItemCriteria() {
		this(1, 8);
	}
	
	public ItemCriteria(int page, int items) {
		this.page = page;
		this.items = items;
	}
	
	public String getListLink() {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("")
				.queryParam("page", page)
				.queryParam("items", items)
				.queryParam("section", section)
				.queryParam("sortBy", this.sortBy);
		return builder.toUriString();
	}
}
