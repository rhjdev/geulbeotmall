/* 전체 선택 */
$('.checkAll').click(function(){
    if($('.checkAll').is(':checked')) {
        $('.item').prop('checked', true);
    } else {
        $('.item').prop('checked', false);
    }
});

$('.item').click(function(){
    if($('input[name=checkItem]:checked').length == $('.item').length) {
        $('.checkAll').prop('checked', true);
    } else {
        $('.checkAll').prop('checked', false);
    }
});

/* 탭 이동 시 체크박스 초기화 */
$('.nav-link').click(function(){
	$('.checkAll').prop('checked', false);
	$('.item').prop('checked', false);
});

/* 배송상태관리 */
function manageDeliveryStatus() {
	let dlvrValue = document.getElementById('dlvrValue').value;
	let checkbox = $('input[name=checkItem]:checked');
	let orderNo = "";
	let arr = new Array();
	//1-1. 전체목록에서 상태변경
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			let td = tr.children();
			console.log(td);
			orderNo = td.eq(6)[0].attributes.value.textContent; //td 태그 중 6번 index에 해당하는 value
			console.log(orderNo);
			arr.push(orderNo);
		});
	}
	//1-2. 전체목록에서 배송상태변경 시도 중 선택된 체크박스가 없음
	if(orderNo == "") {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 주문을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	//2. 상세정보에서 배송상태변경
	
	$.ajax({
		url : '/admin/order/manageDeliveryStatus',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : {
			dlvrValue : dlvrValue,
			arr : arr
		},
		success : function(result){
			if(result == 'succeed'){
				Swal.fire({
					icon: 'success',
					title: '배송상태가 변경되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						history.go(0); //현재 페이지 새로고침
					}
				})
			} else {
				Swal.fire({
					icon: 'error',
					title: result,
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						history.go(0);
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
}

/* 새로고침 */
function refreshList() {
	window.location.assign('/admin/product/list');
}