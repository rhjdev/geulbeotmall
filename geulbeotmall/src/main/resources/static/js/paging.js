$(document).ready(function() {
	/* 페이징 폼 제출 */
	let pageForm = $("#pageForm");
	$(".page-item a").on("click", function(e){
		e.preventDefault();
		console.log('click');
		pageForm.find("input[name='currentPageNo']").val($(this).attr("href"));
		pageForm.submit();
	});
	/* 검색 폼 제출 */
	let searchForm = $("#searchForm");
	$("#searchForm button").on("click", function(e){
		searchForm.find("input[name='currentPageNo']").val("1");
		e.preventDefault();
		searchForm.submit();
	});
});