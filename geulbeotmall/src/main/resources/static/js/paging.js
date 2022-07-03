/* 페이징 폼 제출 */
$(document).ready(function() {
	let pageForm = $("#pageForm");
	$(".page-item a").on("click", function(e){
		e.preventDefault();
		console.log('click');
		pageForm.find("input[name='currentPageNo']").val($(this).attr("href"));
		pageForm.submit();
	});
});