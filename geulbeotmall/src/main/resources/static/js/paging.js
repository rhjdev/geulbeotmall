$(document).ready(function() {
	/* 페이징 폼 제출 */
	let pageForm = $("#pageForm");
	let sortingForm = $('#sortingForm');
	$(".page-item a").on("click", function(e){
		e.preventDefault();
		console.log('click');
		/* table pagination */
		if(pageForm) {
			pageForm.find("input[name='currentPageNo']").val($(this).attr("href"));
			pageForm.submit();
		}
		/* items pagination */
		if(sortingForm) {
			sortingForm.find("input[name='page']").val($(this).attr("href"));
			sortingForm.submit();
		}
	});
	/* 검색 폼 제출 */
	let searchForm = $("#searchForm");
	$("#searchForm button").on("click", function(e){
		searchForm.find("input[name='currentPageNo']").val("1");
		e.preventDefault();
		searchForm.submit();
	});
});