/* 비밀번호 표시 */
$('.visibility i').click(function(){
	const password = document.querySelector('#memberPwd');
	const confirmPwd = document.querySelector('#confirmPwd');
	
	if(password.type === 'password' && confirmPwd.type === 'password') {
		password.type = 'text';
		confirmPwd.type = 'text';
		$(this).attr('class', 'fa-solid fa-eye');
		$(this).attr('class', 'fa-solid fa-eye').css('filter', 'invert(7%) sepia(98%) saturate(7437%) hue-rotate(243deg) brightness(78%) contrast(132%)');
    } else {
		password.type = 'password';
		confirmPwd.type = 'password';
		$(this).attr('class', 'fa-solid fa-eye-slash');
		$(this).attr('class', 'fa-solid fa-eye-slash').css('filter', 'invert(47%) sepia(2%) saturate(2314%) hue-rotate(167deg) brightness(95%) contrast(80%)');
    }
});

/* 이메일 자동완성 */
function autoDomain(email, value){
    let emailId = value.split('@');
    let domainList = ['naver.com','gmail.com','hanmail.net','kakao.com','hotmail.com','nate.com','msn.com'];
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

/* 전체 동의 */
$('#checkAll').click(function(){
    if($('#checkAll').is(':checked')) {
        $('.term').prop('checked', true);
    } else {
        $('.term').prop('checked', false);
    }
});

$('.term').click(function(){
    if($('input[class=term]:checked').length == $('.term').length) {
        $('#checkAll').prop('checked', true);
    } else {
        $('#checkAll').prop('checked', false);
    }
});