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

/* 삭제글 복구 */
function restorePost() {
	let checkbox = $('input[name=checkItem]:checked');
	let trashNo = "";
	let arr = new Array();
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			trashNo = tr.children()[1].attributes[0].textContent;
			arr.push(trashNo);
		});
	} else {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 삭제글을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	
	$.ajax({
		url : '/admin/post/restorePost',
		type : 'post',
		traditional: true,
		dataType : 'text',
		data : { arr : arr },
		success : function(result){
			if(result == 'succeed'){
				Swal.fire({
					icon: 'success',
					title: '게시글 복구가 완료되었습니다',
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
}

/* 휴지통 삭제글로 지정 */
function moveToTrash() {
	console.log(admin);
	let checkbox = $('input[name=checkItem]:checked');
	let reviewNo = "";
	let revwTitle = "";
	let memberId = "";
	let noArr = new Array();
	let titleArr = new Array();
	let writerArr = new Array();
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			reviewNo = tr.children()[1].attributes[0].textContent;
			revwTitle = tr.children()[1].innerText;
			console.log(revwTitle);
			memberId = tr.children().eq(3).text();
			noArr.push(reviewNo);
			titleArr.push(revwTitle);
			writerArr.push(memberId);
		});
	} else {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 게시글을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	
	$.ajax({
		url : '/admin/post/moveToTrash',
		type : 'post',
		traditional: true,
		dataType : 'text',
		data : {
			noArr : noArr,
			titleArr : titleArr,
			writerArr : writerArr,
			admin : admin
		},
		success : function(result){
			if(result == 'succeed'){
				Swal.fire({
					icon: 'success',
					title: '게시글이 휴지통으로 옮겨졌습니다',
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
}

/* 계정정지 */
function suspendAcc() {
	let checkbox = $('input[name=checkItem]:checked');
	let memberId = "";
	let accSuspDesc = "";
	if(checkbox.length == 1) {
		let tr = checkbox.parent().parent().eq(0);
		let td = tr.children();
		memberId = td.eq(3).text();
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
	}
	
	if(checkbox.length != 1) {
		Swal.fire({
			icon: 'warning',
			title: '1개의 계정을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
}

/* 새로고침 */
function refreshList() {
	window.location.assign('/admin/post/list');
}