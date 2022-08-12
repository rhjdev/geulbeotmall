/* 전체 선택 */
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

/* 수량 증가 */
$(document).on('click', '.upBtn', function(){ //up 버튼
	let selectedAmount = $(this).closest('.countBox').find('input[name=selectedAmount]').val(); //input
	console.log("selectedAmount : " + selectedAmount);
	let count = parseInt(selectedAmount);
	count++;
	
	//클릭 시 색상 변경으로 활성화 표시
	if(count > 1) {
		$(this).closest('td').find('.downBtn').prop('disabled', false);
	}
	
	$(this).attr('style', 'color: #000;');
	$(this).closest('td').find('.downBtn').attr('style', 'color: #000;');
	$(this).closest('td').find('.modifyBtn').attr('style', 'color: #000; border: 1px solid #000;');
	
	$(this).closest('.countBox').find('input[name=selectedAmount]').val(count); //증가한 수량 대입
	
	//수량 증가에 따른 주문금액 반영
	let originalPrice = $(this).closest('tr').find('.prodPrice').attr('value');
	console.log(originalPrice);
	let price = parseInt(originalPrice);
	let result = count * price;
	$(this).closest('tr').find('.orderPrice').attr('value', result);
	$(this).closest('tr').find('.orderPrice').text(result.toLocaleString('ko-KR') + "원"); //원화 단위로 출력
});

/* 수량 감소 */
$(document).on('click', '.downBtn', function(){ //down 버튼
	let selectedAmount = $(this).closest('.countBox').find('input[name=selectedAmount]').val(); //input
	//console.log("selectedAmount : " + selectedAmount);
	let count = parseInt(selectedAmount);
	count--;
	if(count == 1) {
		$(this).closest('td').find('.downBtn').prop('disabled', true);
	} else {
		$(this).attr('style', 'color: #000;');
		$(this).closest('td').find('.upBtn').attr('style', 'color: #000;');
		$(this).closest('td').find('.modifyBtn').attr('style', 'color: #00008b; border: 1px solid #000;');
	}
	
	$(this).closest('.countBox').find('input[name=selectedAmount]').val(count); //감소한 수량 대입
	
	//수량 감소에 따른 주문금액 반영
	let originalPrice = $(this).closest('tr').find('.prodPrice').attr('value');
	console.log(originalPrice);
	let price = parseInt(originalPrice);
	let result = count * price;
	$(this).closest('tr').find('.orderPrice').attr('value', result);
	$(this).closest('tr').find('.orderPrice').text(result.toLocaleString('ko-KR') + "원"); //원화 단위로 출력
});

/* 수량 입력 */
$(document).on('change', 'input[name=selectedAmount]', function(){ //input 값 변경
	let selectedAmount = $(this).closest('.countBox').find('input[name=selectedAmount]').val(); //input
	let count = parseInt(selectedAmount);
	if(count > 1) {
		$(this).closest('td').find('.downBtn').prop('disabled', false);
	} else if(count == 1) {
		$(this).closest('td').find('.downBtn').prop('disabled', true);
	}
	
	$(this).closest('td').find('.upBtn').attr('style', 'color: #000;');
	$(this).closest('td').find('.downBtn').attr('style', 'color: #000;');
	$(this).closest('td').find('.modifyBtn').attr('style', 'color: #000; border: 1px solid #000;');
	
	//수량 입력에 따른 주문금액 반영
	let originalPrice = $(this).closest('tr').find('.prodPrice').attr('value');
	console.log(originalPrice);
	let price = parseInt(originalPrice);
	let result = count * price;
	$(this).closest('tr').find('.orderPrice').attr('value', result);
	$(this).closest('tr').find('.orderPrice').text(result.toLocaleString('ko-KR') + "원"); //원화 단위로 출력
});

/* 수량 변경사항 저장 */
$(document).on('click', '.modifyBtn', function(){ //modify 버튼
	let selectedAmount = $(this).closest('tr').find('input[name=selectedAmount]').val(); //input
	let optionNo = $(this).closest('tr').find('.option-area').attr('value');
	console.log(optionNo);
	
	let param = { quantity : selectedAmount, optionNo : optionNo };
	
	$.ajax({
		url : '/cart/mycart/modify',
		type: 'post',
		data : JSON.stringify(param),
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		success : function(result){
			Swal.fire({
				icon: 'success',
				title: '수량 변경이 완료되었습니다',
				confirmButtonColor: '#00008b',
				confirmButtonText: '확인'
			}).then((result) => {
				if(result.isConfirmed) {
					window.location.reload(); //페이지 새로고침
					window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
				}
			})
		},
		error : function(status, error){ console.log(status, error); }
	});
});

/* 선택 상품 행 삭제 */
$(document).on('click', '.deleteBtn', function(){ //delete 버튼
	let optionNo = $(this).closest('tr').find('.option-area').attr('value');
	
	let param = { optionNo : optionNo };
	
	$.ajax({
		url : '/cart/mycart/delete',
		type: 'post',
		data : JSON.stringify(param),
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		success : function(result){
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
		},
		error : function(status, error){ console.log(status, error); }
	});
});

/* 선택 상품 목록 삭제 */
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
		url : '/cart/mycart/deleteAll',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : { arr : arr },
		success : function(result){
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
		},
		error : function(status, error){ console.log(status, error); }
	});
});

/* 선택 상품 찜하기 */
function addToWishList() { //wish 버튼
	let checkbox = $('input[name=checkItem]:checked');
	let optionNo = "";
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
	//1-3. 로그인 여부 확인
	} else if(arr.length > 0 && !document.getElementById('isLoggedInAs')) {
		Swal.fire({
			icon: 'warning',
			title: '로그인이 필요합니다',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				location.href='/member/signin';
			}
		})
	} else {
		
	$.ajax({
		url : '/member/wishlist/add',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : { arr : arr },
		success : function(result){
			if(result == '성공') {
				Swal.fire({
					icon: 'success',
					title: '찜하기가 완료되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						window.location.reload(); //페이지 새로고침
						window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
					}
				})
			} else {
				Swal.fire({
					icon: 'error',
					title: '이미 찜하기 목록에 존재하는 상품입니다',
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
	}
	
};

/* 장바구니 합계 */
let quantity = 0;

/* 합계A. 상품금액 */
let prodPrice = document.querySelectorAll('.origPrice');
let orderPrice = 0;
//console.log(prodPrice);
for(let i=0; i < prodPrice.length; i++) {
	quantity = prodPrice[i].parentElement.parentElement.children[4].children[0].children[1].value; //선택수량
	//console.log(prodPrice[i].attributes.value.textContent); //NodeList에서 value 추출
	orderPrice += (parseInt(prodPrice[i].attributes.value.textContent) * quantity); //int 타입으로 변환
	//console.log(orderPrice);
}
	
/* 합계B. 할인금액 */
let discounts = document.querySelectorAll('del');
let discounted = document.querySelectorAll('.discounted');
let sellingPrice = 0;
let savingPrice = 0;
for(let i=0; i < discounts.length; i++) { //할인상품 개수만큼 반복
	quantity = discounts[i].parentElement.parentElement.children[4].children[0].children[1].value; //선택수량
	sellingPrice += (parseInt(discounts[i].attributes.value.textContent) * quantity); //상품금액 총합
	savingPrice +=(parseInt(discounted[i].attributes.value.textContent) * quantity); //할인적용금액 총합
}
let discountAmount = sellingPrice - savingPrice; //총 상품금액 - 총 할인적용금액 = 할인금액

/* 합계C. 배송비 */
let deliveries = document.querySelectorAll('.delivery');
let deliveryFee = 0;
deliveries.forEach(function(item){
	let checkPrice = item.parentElement.children[5].attributes.value.textContent; //cellIndex 통해 불러오기
	//조건1. 기본 3만원 이상 주문 시 무료 배송
	let cost = checkPrice >= 30000 ? 0 : 2500;
	//조건2. 일부 브랜드는 2만원 이상일 때 무료 배송하며 기본금 상이
	if(item.parentElement.children[6].innerHTML == '모나미') {
		cost = checkPrice >= 20000 ? 0 : 3000;
	}
	//console.log(checkPrice);
	//console.log(cost);
	item.parentElement.children[7].innerHTML = cost.toLocaleString('ko-KR') + "원"; //원화 단위로 출력
	item.parentElement.children[7].attributes.value = cost;
	//console.log(item.parentElement.children);
	deliveryFee += parseInt(item.parentElement.children[7].attributes.value); //배송비 총합
});

/* 합계 반영 */
let totalPrice = orderPrice - discountAmount + deliveryFee;
document.querySelector('.order-price').innerHTML = orderPrice.toLocaleString('ko-KR');
document.querySelector('.discount-amount').innerHTML = discountAmount.toLocaleString('ko-KR');
document.querySelector('.delivery-fee').innerHTML = deliveryFee.toLocaleString('ko-KR');
document.querySelector('.total-price').innerHTML = totalPrice.toLocaleString('ko-KR');

/* 주소 API */
function DaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            let detail = document.getElementById('detailAddress').value;
            document.getElementById('postalCode').value = data.zonecode;
            document.getElementById('address').value = data.roadAddress;
            if(data.buildingName != '') {
                document.getElementById('detailAddress').value = data.buildingName;
            }
            document.getElementById('detailAddress').focus();
        }
    }).open();
}

/* 주문자 정보 가져오기 */
$('.check').click(function(){
	let name = $('input[name=hiddenName]').val();
	let phone = $('input[name=hiddenPhone]').val();
	let memberAddress = $('input[name=hiddenAddress]').val();
	let addressArr = memberAddress.split('$');
	let postalCode = addressArr[0];
	let address = addressArr[1];
	let detailAddress = addressArr[2];
	
	if($('.check').is(':checked')) {
		$('input[name=rcvrName]').val(name);
		$('input[name=rcvrPhone]').val(phone);
		$('input[name=postalCode]').val(postalCode);
		$('input[name=address]').val(address);
		$('input[name=detailAddress]').val(detailAddress);
    } else {
		$('input[name=rcvrName]').val('');
		$('input[name=rcvrPhone]').val('');
		$('input[name=postalCode]').val('');
		$('input[name=address]').val('');
		$('input[name=detailAddress]').val('');
    }
});

/* 옵션 드롭다운 */
$('.dropdown').on('click', function() {
	$('.dropdown').not($(this)).removeClass('open');
	$(this).toggleClass('open');
	if ($(this).hasClass('open')){
		$(this).find('.option').attr('tabindex', 0);
		$(this).find('.selected').focus();
	} else {
		$(this).find('.option').removeAttr('tabindex');
		$(this).focus();
	}
});

/* 드롭다운 옵션 선택 시 */
$('.dropdown .option').on('click', function() {
	let text = $(this).data('display-text') || $(this).html();
	$(this).closest('.dropdown').find('.current').html(text); //선택값 반영
	
	if(text == '직접 입력') {
		$('#typeOwnMessage').prop('hidden', false);
	}
});

/* Modal */
$('.interest-free-detail').on('click', function(){
	$('#interest-free').modal('show');
	return false;
});

/* Login 확인 */
function isLoggedIn(){
	if(document.getElementById('isLoggedInAs')) {
		console.log('존재함');
	} else {
		Swal.fire({
			icon: 'warning',
			title: '로그인이 필요합니다',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				location.href='/member/signin';
			}
		})
		return;
	}
}