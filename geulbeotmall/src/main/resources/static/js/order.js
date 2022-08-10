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

/* 수량 증가 */
$(document).on('click', '.button-up', function(){ //up 버튼
	let selectedAmount = $(this).closest('.countBox').find('input[name=selectedAmount]').val(); //input
	console.log("selectedAmount : " + selectedAmount);
	let count = parseInt(selectedAmount);
	count++;
	
	//클릭 시 색상 변경으로 활성화 표시
	if(count > 1) {
		$(this).closest('td').find('.button-down').prop('disabled', false);
	}
	
	$(this).attr('style', 'color: #00008b;');
	$(this).closest('td').find('.button-down').attr('style', 'color: #00008b;');
	$(this).closest('td').find('.button-modify').attr('style', 'color: #00008b; border: 1px solid #00008b;');
	
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
$(document).on('click', '.button-down', function(){ //down 버튼
	let selectedAmount = $(this).closest('.countBox').find('input[name=selectedAmount]').val(); //input
	//console.log("selectedAmount : " + selectedAmount);
	let count = parseInt(selectedAmount);
	count--;
	if(count == 1) {
		$(this).closest('td').find('.button-down').prop('disabled', true);
	} else {
		$(this).attr('style', 'color: #00008b;');
		$(this).closest('td').find('.button-up').attr('style', 'color: #00008b;');
		$(this).closest('td').find('.button-modify').attr('style', 'color: #00008b; border: 1px solid #00008b;');
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
		$(this).closest('td').find('.button-down').prop('disabled', false);
	} else if(count == 1) {
		$(this).closest('td').find('.button-down').prop('disabled', true);
	}
	
	$(this).closest('td').find('.button-up').attr('style', 'color: #00008b;');
	$(this).closest('td').find('.button-down').attr('style', 'color: #00008b;');
	$(this).closest('td').find('.button-modify').attr('style', 'color: #00008b; border: 1px solid #00008b;');
	
	//수량 입력에 따른 주문금액 반영
	let originalPrice = $(this).closest('tr').find('.prodPrice').attr('value');
	console.log(originalPrice);
	let price = parseInt(originalPrice);
	let result = count * price;
	$(this).closest('tr').find('.orderPrice').attr('value', result);
	$(this).closest('tr').find('.orderPrice').text(result.toLocaleString('ko-KR') + "원"); //원화 단위로 출력
});

/* 수량 변경사항 저장 */
$(document).on('click', '.button-modify', function(){ //modify 버튼
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