/* 마이페이지 네비게이션 */
let link = document.location.href;
let nav = document.querySelectorAll('.mypage-nav > ul > li > .nav-link');
//console.log(nav);
switch(true) {
	case link.indexOf('main') > 0: nav[0].className = 'nav-link active'; break;
	case link.indexOf('order') > 0: nav[1].className = 'nav-link active'; break;
	case link.indexOf('review') > 0: nav[2].className = 'nav-link active'; break;
	case link.indexOf('wishlist') > 0: nav[3].className = 'nav-link active'; break;
	case link.indexOf('reserve') > 0: nav[4].className = 'nav-link active'; break;
	case link.indexOf('change') > 0: nav[5].className = 'nav-link active'; break;
	case link.indexOf('inactivate') > 0: nav[6].className = 'nav-link active'; break;
}

/* 위시리스트 항목 전체 선택 */
$(document).on('click', 'input[name=checkAll]', function(){
	for(let i=0; i < $('input[name=checkAll]').length; i++) { //2개의 전체선택 체크박스
		if($('input[name=checkAll]:checked').length != $('input[name=checkAll]').length) { //선택 또는 해제 일괄 적용
			$('input[name=checkAll]')[i].checked = true;
	        $('.item').prop('checked', true);
	    } else {
			$('input[name=checkAll]')[i].checked = false;
	        $('.item').prop('checked', false);
	        $(this).prop('checked', false);
	    }
	}
});

$('.item').click(function(){
    if($('input[name=checkItem]:checked').length == $('.item').length) {
        $('.checkAll').prop('checked', true);
    } else {
        $('.checkAll').prop('checked', false);
    }
});

/* 위시리스트 단일 상품 주문 */
let buttonClicked;
let tr;
for(const item of document.querySelectorAll('.orderBtn')) {
	item.addEventListener('click', function(event){
		buttonClicked = event.target;
		console.log(buttonClicked);
		tr = event.target.parentElement.parentElement.parentElement; //행 기준으로 탐색
		
		let optionNo = "";
		optionNo = tr.children[2].attributes.value.textContent;
		console.log(optionNo);
		
		$.ajax({
			url : '/cart/order',
			type: 'get',
			data : { optionNoFromWishList : optionNo },
			success : function(result){
				console.log('주문페이지 이동');
				location.href='/cart/order';
			},
			error : function(status, error){ console.log(status, error); }
		});
	});
};

/* 위시리스트에서 장바구니로 담기 */
let orderOptionNo = new Array(); //주문용 번호
let orderOptionQt = new Array(); //주문용 수량
for(const item of document.querySelectorAll('.cartBtn')) {
	item.addEventListener('click', function(event){
		buttonClicked = event.target;
		console.log(buttonClicked);
		tr = event.target.parentElement.parentElement.parentElement; //행 기준으로 탐색
		
		let optionNo = "";
		optionNo = tr.children[2].attributes.value.textContent;
		console.log(optionNo);
		orderOptionNo.push(optionNo);
		orderOptionQt.push(1); //기본 선택 수량
	
		$.ajax({
			url : '/cart/mycart/add',
			type : 'post',
			traditional : true, //배열 넘기기 위한 세팅
			dataType : 'text',
			data : {
				orderOptionNo : orderOptionNo,
				orderOptionQt : orderOptionQt
			},
			success : function(result){
				if(result == '성공'){
					Swal.fire({
						icon: 'success',
						title: '장바구니에 담았습니다',
						text: '장바구니로 이동하시겠습니까?',
						showCloseButton: true,
						showDenyButton: true,
						denyButtonColor: '#6c757d',
						denyButtonText: '취소',
						confirmButtonColor: '#00008b',
						confirmButtonText: '이동',
						reverseButtons: true
					}).then((result) => {
						if(result.isConfirmed) {
							window.location.href='/cart/mycart';
						}
					})
				} else {
					Swal.fire({
						icon: 'warning',
						title: result,
						text: '해당 상품의 수량을 추가했습니다',
						confirmButtonColor: '#00008b',
						confirmButtonText: '확인'
					}).then((result) => {
						if(result.isConfirmed) {}
					})
				}
			},
			error : function(status, error){ console.log(status, error); }
		});
	})
};

/* 위시리스트 선택 상품 행 삭제 */
$(document).on('click', '.deleteBtn', function(){ //delete 버튼
	let optionNo = $(this).closest('tr').find('.option-area').attr('value');
	
	let param = { optionNo : optionNo };
	
	$.ajax({
		url : '/mypage/wishlist/delete',
		type: 'post',
		dataType: 'text',
		data : JSON.stringify(param),
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		success : function(result){
			if(result == 'succeed') {
				Swal.fire({
					icon: 'success',
					title: '위시리스트에서 삭제되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						window.location.reload(); //페이지 새로고침
						window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
});

/* 위시리스트 선택 목록 삭제 */
$(document).on('click', '.button-delete', function(){ //delete 버튼
	let checkbox = $('input[name=checkItem]:checked');
	let optionNo = 0;
	let arr = new Array();
	//1-1. 전체목록 중 선택한 옵션번호 저장
	checkbox.each(function(i){
		let tr = checkbox.parent().parent().eq(i);
		let td = tr.children();
		optionNo = td.eq(2).attr('value');
		console.log(optionNo);
		arr.push(optionNo);
	});
	//1-2. 선택된 체크박스가 없는 경우
	if(arr.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 상품을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0); //현재 페이지 새로고침
			}
		})
	}
	
	$.ajax({
		url : '/mypage/wishlist/deleteAll',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : { arr : arr },
		success : function(result){
			if(result == 'succeed') {
				Swal.fire({
					icon: 'success',
					title: '선택 상품이 삭제되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						window.location.reload(); //페이지 새로고침
						window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
});

/* 회원정보수정 */
$(document).on('change', 'input[class=phone]', function(){ //연락처 input 값 변경
	$(this).closest('div').find('.verifyBtn').attr('style', 'color: #000; border: 1px solid #000;');
	$(this).closest('tbody').find('input[name=verifyPhone]').attr('style', 'color: #000; border: 1px solid #000;');
});

$(document).on('change', 'input[name=email]', function(){ //이메일 input 값 변경
	$(this).closest('div').find('.verifyBtn').attr('style', 'color: #000; border: 1px solid #000;');
	$(this).closest('tbody').find('input[name=verifyEmail]').attr('style', 'color: #000; border: 1px solid #000;');
});