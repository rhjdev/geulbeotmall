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
	case link.indexOf('close') > 0: nav[6].className = 'nav-link active'; break;
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

/* 휴대폰 본인인증 */
let isAuthenticatedMember = document.getElementById('isAuthenticatedMember'); //true or false
$(document).on('change', 'input[class=phone]', function(){ //연락처 input 값 변경
	let registered = '[[${detail.phone}]]'
	let phone = document.getElementById('phoneA').value + document.getElementById('phoneB').value + document.getElementById('phoneC').value;
	console.log(registered);
	//console.log(phone);
	if(registered != phone) {
		$(this).closest('div').find('.verifyBtn').attr('value', '인증번호전송');
		$(this).closest('tbody').find('.typeVerifyPhoneNumber').attr('style', 'display: contents;');
	}
});

function verifyPhone() {
	let phone = document.getElementById('phoneA').value + document.getElementById('phoneB').value + document.getElementById('phoneC').value;
	if(isAuthenticatedMember) { //이미 인증된 연락처인 경우 버튼 비활성화
		return false;
	} else {
		/* 1. 인증번호전송 */
		$.ajax({
			url : '/member/checkPhone',
			type : 'post',
			data : { phone : phone },
			success : function(data) {
				let number = data; //전송된 인증번호
				Swal.fire({
					icon: 'success',
					title: '휴대폰으로 본인확인용 인증번호가 전송되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) { console.log('인증번호 전송완료'); }
				})
				/* 2. 인증하기 */
				$('.submitNumberBtn').click(function(){
					let submitted = document.getElementById('verificationNumber').value; //사용자의 입력값
					if(number == submitted) {
						$.ajax({
							url : '/member/authenticated',
							type : 'post',
							data : { result : 'Y' },
							/* 2-1. 인증 성공 시 버튼에 '인증완료' 출력 및 비활성화 */
							success : function(data) {
								if(data == 'succeed') {
									Swal.fire({
										icon: 'success',
										title: '휴대폰 본인인증에 성공하였습니다',
										confirmButtonColor: '#00008b',
										confirmButtonText: '확인'
									}).then((result) => {
										if(result.isConfirmed) {
											history.go(0);
										}
									})
									$(this).closest('div').find('.verifyBtn').attr('value', '인증완료');
									$(this).closest('div').find('.verifyBtn').attr('disabled', 'disabled');
									$(this).closest('tbody').find('.typeVerifyPhoneNumber').attr('style', 'display: none;');
								} else {
									Swal.fire({
										icon: 'error',
										title: '잠시 후 다시 시도해 주세요',
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
					} else {
						Swal.fire({
							icon: 'error',
							title: '인증번호가 일치하지 않습니다',
							confirmButtonColor: '#00008b',
							confirmButtonText: '확인'
						}).then((result) => {
							if(result.isConfirmed) {
								console.log('인증번호오류');
							}
						})
					}
				});
			},
			error : function(status, error){ console.log(status, error); }
		});
	}
}

/* 이메일 인증 */
$(document).on('change', 'input[name=email]', function(){ //이메일 input 값 변경
	let registered = document.getElementById('registeredEmail').value;
	let email = document.getElementById('email').value;
	//console.log(registered);
	//console.log(email);
	if(registered != email) { //회원가입 중 이메일 인증 치르므로 기본값 '인증완료', 등록된 이메일 주소에서 변경사항 발생 시 '인증코드전송'
		$(this).closest('div').find('.verifyBtn').attr('value', '인증코드전송');
		$(this).closest('tbody').find('.typeVerifyEmailCode').attr('style', 'display: contents;');
	} else {
		$(this).closest('div').find('.verifyBtn').attr('value', '인증완료');
		$(this).closest('tbody').find('.typeVerifyEmailCode').attr('style', 'display: none;');
	}
});

/* 이메일 자동완성 */
function autoDomain(email, value){
    let emailId = value.split('@');
    let domainList = ['naver.com','gmail.com','kakao.com','outlook.com','hotmail.com','nate.com','msn.com'];
    let availableBox = new Array; // 자동완성 키워드 리스트
    for(let i=0; i < domainList.length; i++ ){
        availableBox.push( emailId[0] +'@'+ domainList[i] );
    }
    $("#"+email).autocomplete({
        source: availableBox,
        focus: function(event, ui) {
            return false;
        }
    });
}

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

/* 회원정보수정 폼 제출 */
function submitChangeForm() { //업데이트할 내용이 없는 input 항목은 disabled 속성 명시하여 제외
	event.preventDefault();
	let form = document.getElementById('changeInfoForm');
	
	let agreement = document.querySelector('input[name=agreement]:checked').value; //Y/N 선택값 전달
	
	let newPwd = document.querySelector('input[name=newPwd]').value;
	if(newPwd != '') { //새 비밀번호 작성 시에 한하여 유효성 검사
		/* 1. 영문, 숫자, 특수기호 포함 여부 검사 */
		const regexp = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$/;
		/* 2. 아이디와 연속 일치 여부 검사 */
		let memberPwd = document.querySelector('input[name=memberPwd]').value;
		let id = document.querySelector('input[name=memberId]').value;
		let tmp = '';
		let count = 0;
		for(i=0; i < id.length-2; i++) {
			tmp = id.charAt(i) + id.charAt(i+1) + id.charAt(i+2);
			if(newPwd.indexOf(tmp) > -1) { count = count + 1 };
		}
		if(!regexp.test(newPwd)) {
			alert('비밀번호는 8~16자의 영문 대소문자/숫자/특수기호 조합하여 입력하세요');
		} if(count > 0) {
			alert('아이디와 연속 3자리 이상 일치하는 비밀번호는 사용할 수 없어요');
		/* 3. 현재 비밀번호와 일치 여부 검사 */
		} else if(memberPwd == newPwd) {
			alert('현재 사용 중인 비밀번호로는 변경할 수 없습니다');
		} else {
			form.submit();
		}
	} else {
		form.submit();
	}
}

/* 회원탈퇴 폼 제출 */
function submitCloseForm() {
	event.preventDefault();
	let form = document.getElementById('closeForm');
	let isChecked = document.getElementById('check').checked;
	let memberPwd = document.getElementById('memberPwd').value;
	if(memberPwd.length == 0) { //1. 비밀번호 확인
		Swal.fire({
			icon: 'warning',
			title: '비밀번호를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {}
		})
	} else {
		if(isChecked) { //2. 체크박스 선택한 상황에 한하여 탈퇴 진행
			form.submit();
		} else {
			Swal.fire({
				icon: 'warning',
				title: '탈퇴 전 안내사항을 읽고 체크박스를 선택하세요',
				confirmButtonColor: '#00008b',
				confirmButtonText: '확인'
			}).then((result) => {
				if(result.isConfirmed) {}
			})
		}
	}
}