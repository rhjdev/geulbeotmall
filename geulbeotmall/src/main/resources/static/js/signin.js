/* 아이디 저장 */
let link = document.location.href;
if(link.indexOf('signin') >= 0) {
	const memberId = document.querySelector("#username");
	const save = document.querySelector("#rememberId");
	
	let cookieId = getCookie("cookieId"); //저장된 쿠키값 호출, 아이디 입력란에 해당 값 삽입(없을 경우 공백)
	memberId.value = cookieId;
	
	if(memberId.value != "") { //아이디 input 태그에 값이 삽입되면 체크박스 활성화
		save.checked = true;
	}
	
	save.addEventListener('change', function(){ //체크박스 체크 시 7일간 보관, 해제 시 쿠키 삭제
		if(this.checked) {
			setCookie("cookieId", memberId.value, 7);
		} else {
			deleteCookie("cookieId");
		}
	});
	
	memberId.addEventListener('keyup', function(){ //체크박스 체크 후 아이디 입력 시 7일간 보관
		if(save.checked) {
			setCookie("cookieId", memberId.value, 7);
		}
	});
}

function setCookie(cookieName, value, days){ //쿠키 저장(이름, 값, 유효일자)
	let expDate = new Date();
	expDate.setDate(expDate.getDate() + days);
	
	let cookieValue = escape(value) + ((days == null) ? "" : "; expires=" + expDate.toGMTString());
	
	document.cookie = cookieName + "=" + cookieValue;
};

function deleteCookie(cookieName) { //쿠키 삭제(이름)
	let expireDate = new Date();
	expireDate.setDate(expireDate.getDate() - 1);
	document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}

function getCookie(cookieName) { //쿠키 호출(이름)
	cookieName = cookieName + '=';
	let cookieData = document.cookie;
	
	let start = cookieData.indexOf(cookieName);
	let cookieValue = '';
	if(start != -1) {
		start += cookieName.length;
		let end = cookieData.indexOf(';', start);
		if(end == -1) {
			end = cookieData.length;
		}
		cookieValue = cookieData.substring(start, end);
	}
	return unescape(cookieValue);
}

/* 비밀번호 표시 */
$('.visibility i').on('click', function(){
	const password = document.querySelector('#password');
	
    if(password.type === 'password') {
        password.type = 'text';
        $(this).attr('class', 'fa-solid fa-eye');
        $(this).attr('class', 'fa-solid fa-eye').css('filter', 'invert(7%) sepia(98%) saturate(7437%) hue-rotate(243deg) brightness(78%) contrast(132%)');
    } else {
        password.type = 'password';
        $(this).attr('class', 'fa-solid fa-eye-slash');
        $(this).attr('class', 'fa-solid fa-eye-slash').css('filter', 'invert(47%) sepia(2%) saturate(2314%) hue-rotate(167deg) brightness(95%) contrast(80%)');
    }
});

/* 아이디/비밀번호찾기 폼 제출 */
function submitFindForm() {
	event.preventDefault();
	let target = event.target.className;
	if(target.indexOf('findIdBtn') >= 0) { //아이디 찾기
		let form = document.getElementById('findIdForm');
		let input = document.querySelectorAll('input[class=id-input]');
		let count = 0;
		input.forEach((input, index) => { if(input.value.length != 0) count++; });
		if(input.length == count) {
			form.submit();
		} else {
			Swal.fire({
				icon: 'warning',
				title: '이름, 이메일을 모두 입력해 주세요',
				confirmButtonColor: '#00008b',
				confirmButtonText: '확인'
			}).then((result) => {
				if(result.isConfirmed) {}
			})
		}
	} else { //비밀번호 찾기
		let form = document.getElementById('findPwdForm');
		let input = document.querySelectorAll('input[class=pwd-input]');
		let count = 0;
		input.forEach((input, index) => { if(input.value.length != 0) count++; });
		if(input.length == count) {
			form.submit();
		} else {
			Swal.fire({
				icon: 'warning',
				title: '아이디, 이름, 이메일을 모두 입력해 주세요',
				confirmButtonColor: '#00008b',
				confirmButtonText: '확인'
			}).then((result) => {
				if(result.isConfirmed) {}
			})
		}
	}
}