$(document).ready(function() {
	//Select2 적용
	$('#prodInfo').select2();
	$('#bodyColor').select2();
	$('#inkColor').select2();
	$('#pointSize').select2();
	
	//오늘 날짜 출력(백틱 사용)
	let today = new Date();
	let year = today.getFullYear();
	let month = today.getMonth() + 1;
	let date = today.getDate();
	today = year + '-' + month + '-' + date;
	document.getElementById('today').value = "등록일 : " + `${year}-${month >= 10 ? month : '0' + month}-${date >= 10 ? date : '0' + date}`;
});
