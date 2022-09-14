package com.reminder.geulbeotmall.paging.model.dto;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;

@Data
public class ItemCriteria {

	private int page;
	private int items; //per page
	private String section;
	private String category;
	private String brand;
	private int min; //min price
	private int max; //max price
	private String tag;
	private String color; //ink color
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
				.queryParam("category", this.category)
				.queryParam("brand", this.brand)
				.queryParam("min", this.min)
				.queryParam("max", this.max)
				.queryParam("tag", this.tag)
				.queryParam("color", this.color)
				.queryParam("sortBy", this.sortBy);
		return builder.toUriString();
	}
}
