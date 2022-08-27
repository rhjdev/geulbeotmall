/* https://docs.iamport.kr/implementation/payment#add-library */
/* client-side */
function requestPay() {
	let memberId = $('input[name=memberId]').val();
	let memberPhone = $('input[name=phone]').val();
	let memberEmail = $('input[name=email]').val();
	
	let postalCode = $('input[name=postalCode]').val();
	let address = $('input[name=address]').val() + ' ' + $('input[name=detailAddress]').val();
	let message = $('#optionSelector .current').text();
	if(message == '직접 입력') {
		message = $('input[name=typeOwnMessage]').val();
	}
	
	let method = $('input[name=methods]:checked').val();
	let amount = parseInt(document.querySelector('.payment-amount').innerHTML.replace(',', ''));
	let orderName = document.querySelector('.option-area a:first-child').textContent;
	let productCount = document.querySelectorAll('.product-table tbody tr').length; //주문상품 개수
	if(productCount > 1) { //주문상품이 2개 이상인 경우
		orderName = orderName + ' 외 ' + (productCount-1) + '건';
	}
	let paymentNo = 'P' + new Date().getTime() + memberId.toUpperCase();
	
	let rcvrName = $('input[name=rcvrName]').val();
	let rcvrPhone = $('input[name=rcvrPhone]').val();
	let optionNoArr = new Array();
	let options = document.querySelectorAll('.option-area');
	for(let i=0; i < options.length; i++) {
		optionNoArr.push(options[i].attributes.value.textContent);
	}
	let optionQtArr = new Array();
	let quantity = document.querySelectorAll('.quantity-area');
	for(let i=0; i < quantity.length; i++) {
		optionQtArr.push(quantity[i].attributes.value.textContent);
	}
	let orderPriceArr = new Array();
	let price = document.querySelectorAll('.orderPrice');
	for(let i=0; i < quantity.length; i++) {
		orderPriceArr.push(price[i].attributes.value.textContent);
	}
	
	var IMP = window.IMP; // 생략 가능
	IMP.init('imp04584654');
	IMP.request_pay({
		pg: 'html5_inicis',
		pay_method: method,
		merchant_uid: paymentNo,
		name: orderName,
		amount: amount,
		buyer_email: memberEmail,
		buyer_name: memberId,
		buyer_tel: memberPhone,
		buyer_addr: address,
		buyer_postcode: postalCode
	}, function (rsp) { // callback
		console.log(rsp);
		
		//결제 검증
		$.ajax({
			url : '/verifyiamport/' + rsp.imp_uid,
			type : 'post'
		}).done(function(data){
			console.log(data);
			
			if(rsp.paid_amount == data.response.amount) { //클라이언트-서버간 결제금액 비교
				console.log('결제 및 검증 완료');
				
				let params = { optionNoArr : optionNoArr,
							   optionQtArr : optionQtArr,
							   orderPriceArr : orderPriceArr,
							   memberId : memberId,
							   rcvrName : rcvrName,
							   rcvrPhone : rcvrPhone,
							   rcvrPostalCode : postalCode,
							   rcvrAddress : address,
							   dlvrReqMessage : message,
							   paymentNo : paymentNo,
							   paymentMethod : method,
							   paymentAmount : amount };
				
				//주문결제 정보 DB 저장
				$.ajax({
					url : '/cart/order',
					type : 'post',
					traditional : true,
					data : JSON.stringify(params),
					beforeSend : function(xhr) {
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					success : function(result){
						Swal.fire({
							icon: 'success',
							title: '주문 및 결제가 완료되었습니다',
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
			} else {
				var msg = '결제에 실패하였습니다.';
				msg += '에러내용 : ' + rsp.error_msg;
				alert(msg);
			}
		})
	});
}

function test() {
	let memberId = $('input[name=memberId]').val();
	let memberPhone = $('input[name=phone]').val();
	let memberEmail = $('input[name=email]').val();
	
	let postalCode = $('input[name=postalCode]').val();
	let address = $('input[name=address]').val() + ' ' + $('input[name=detailAddress]').val();
	let message = $('#optionSelector .current').text();
	if(message == '직접 입력') {
		message = $('input[name=typeOwnMessage]').val();
	}
	
	let method = $('input[name=methods]:checked').val();
	let amount = parseInt(document.querySelector('.payment-amount').innerHTML.replace(',', ''));
	let orderName = document.querySelector('.option-area a:first-child').textContent;
	let productCount = document.querySelectorAll('.product-table tbody tr').length; //주문상품 개수
	if(productCount > 1) { //주문상품이 2개 이상인 경우
		orderName = orderName + ' 외 ' + (productCount-1) + '건';
	}
	let paymentNo = 'P' + new Date().getTime() + memberId.toUpperCase();
	
	let rcvrName = $('input[name=rcvrName]').val();
	let rcvrPhone = $('input[name=rcvrPhone]').val();
	let optionNoArr = new Array();
	let options = document.querySelectorAll('.option-area');
	for(let i=0; i < options.length; i++) {
		optionNoArr.push(options[i].attributes.value.textContent);
	}
	console.log(optionNoArr);
	let optionQtArr = new Array();
	let quantity = document.querySelectorAll('.quantity-area');
	for(let i=0; i < quantity.length; i++) {
		optionQtArr.push(quantity[i].attributes.value.textContent);
	}
	let orderPriceArr = new Array();
	let price = document.querySelectorAll('.orderPrice');
	for(let i=0; i < quantity.length; i++) {
		orderPriceArr.push(price[i].attributes.value.textContent);
	}
	
	let params = { optionNoArr : optionNoArr,
				   optionQtArr : optionQtArr,
				   orderPriceArr : orderPriceArr,
				   memberId : memberId,
				   rcvrName : rcvrName,
				   rcvrPhone : rcvrPhone,
				   rcvrPostalCode : postalCode,
				   rcvrAddress : address,
				   dlvrReqMessage : message,
				   paymentNo : paymentNo,
				   paymentMethod : method,
				   paymentAmount : amount };
	
	//주문결제 정보 DB 저장
	$.ajax({
		url : '/cart/order',
		type : 'post',
		traditional : true,
		contentType : 'application/json',
		data : JSON.stringify(params),
		success : function(result){
			Swal.fire({
				icon: 'success',
				title: '주문 및 결제가 완료되었습니다',
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
}