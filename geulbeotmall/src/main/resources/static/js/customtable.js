/* 전체 선택 */
$('#checkAll').click(function(){
    if($('#checkAll').is(':checked')) {
        $('.member').prop('checked', true);
    } else {
        $('.member').prop('checked', false);
    }
});

$('.member').click(function(){
    if($('input[name=checkMember]:checked').length == $('.member').length) {
        $('#checkAll').prop('checked', true);
    } else {
        $('#checkAll').prop('checked', false);
    }
});

function manageAuth() {
	let optValue = document.getElementById('authValue').value;
	let checkbox = $('input[name=checkMember]:checked');
	let arr = new Array();
	checkbox.each(function(i){
		let tr = checkbox.parent().parent().eq(i);
		let td = tr.children();
		let memberId = td.eq(2).text();
		arr.push(memberId);
	});
	console.log(optValue);
	console.log(arr);
	
	$.ajax({
		url : '/admin/member/manageAuth',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : {
			optValue : optValue,
			arr : arr
		},
		success : function(result){
			if(result == '성공'){
				Swal.fire({
					icon: 'success',
					title: '권한 변경 완료되었습니다',
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						history.go(0); //현재 페이지 새로고침
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
}

function suspendAcc() {
	let checkbox = $('input[name=checkMember]:checked');
	let memberId = "";
	let accSuspDesc = "";
	if(checkbox.length == 1) {
			let tr = checkbox.parent().parent().eq(0);
			let td = tr.children();
			memberId = td.eq(2).text();
			console.log(memberId);
			
			Swal.fire({
			  title: '선택한 회원을 계정정지 하시겠습니까?',
			  input: 'select',
			  inputOptions: {
			      '개인정보 유출, 사생활 침해': '개인정보 유출, 사생활 침해',
			      '욕설, 인신공격 등': '욕설, 인신공격 등',
			      '같은 내용의 반복 게시(도배)': '같은 내용의 반복 게시(도배)',
			      '기타': '기타'
			  },
			  inputPlaceholder: '계정정지 사유를 선택하세요',
			  confirmButtonColor: '#00008b',
			  confirmButtonText: '확인',
			  inputValidator: (value) => {
				if(!value) {
					return '계정정지 사유를 선택하세요'
				} else {
					accSuspDesc = value,
					console.log(accSuspDesc)
					$.ajax({
						url : '/admin/member/suspendAcc',
						type : 'post',
						traditional : true,
						dataType : 'text',
						data : {
							memberId : memberId,
							accSuspDesc : accSuspDesc,
						},
						success : function(result){
							if(result == '성공') {
								Swal.fire({
									icon: 'success',
									title: '계정 정지 처리되었습니다',
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
					})
				}
	  		}
		})
	} else {
		Swal.fire({
			icon: 'warning',
			title: '1개의 계정만 선택해 주세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
}

function activateAcc() {
	let checkbox = $('input[name=checkMember]:checked');
	let arr = new Array();
	checkbox.each(function(i){
		let tr = checkbox.parent().parent().eq(i);
		let td = tr.children();
		let memberId = td.eq(2).text();
		arr.push(memberId);
	});
	console.log(arr);
	
	$.ajax({
		url : '/admin/member/activateAcc',
		type : 'post',
		traditional : true,
		dataType : 'text',
		data : { arr : arr },
		success : function(result){
			if(result == '성공'){
				Swal.fire({
					icon: 'success',
					title: '계정정지가 해제되었습니다',
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