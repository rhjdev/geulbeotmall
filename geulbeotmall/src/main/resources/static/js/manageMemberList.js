/* 탭 활성화 컨트롤 : html에서 'active' 정의하지 않고 이곳에서 관리 */
$(document).ready(function(){
	let tabs = document.querySelectorAll('button.nav-link');
	let panels = document.querySelectorAll('div.tab-pane');
	
	let activeTab = localStorage.getItem('activeTab') == 'undefined' ? 'total-tab' : localStorage.getItem('activeTab'); //기본 active tab=total-tab
	//console.log(localStorage.getItem('activeTab'));
	
	for(let i=0; i < tabs.length; i++) {
		if(tabs[i].id == activeTab) { tabs[i].classList.add('active'); } //선택 tab 활성화
	}
	for(let i=0; i < panels.length; i++) {
		if(panels[i].attributes['aria-labelledby'].value == activeTab) { panels[i].classList.add('active'); } //aria-labelledby="total-tab"
	}
	
	$('button.nav-link').click(function(){
		/* A. 체크박스 초기화 */
		$('.checkAll').prop('checked', false);
		$('.item').prop('checked', false);
		
		/* B. 새롭게 선택된 탭 정보 저장 */
		localStorage.clear();
		localStorage.setItem('activeTab', $(this).attr('id'));
		activeTab = localStorage.getItem('activeTab');
		
		/* C. 기존 tab/panel에서 'active' 클래스 삭제 */
		for(let i=0; i < panels.length; i++) {
			panels[i].classList.remove('active');
		}
		for(let i=0; i < tabs.length; i++) {
			tabs[i].classList.remove('active'); //기존 active 삭제
		}
		/* D. 각 탭마다 1페이지 고정 */
		let num = document.querySelector('div.paging-wrapper > nav > ul > li:nth-child(2) > a'); //?currentPage=1에 해당하는 pagination num
		num.click();
	});
});

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

/* 권한관리 */
function manageAuth() {
	let optValue = document.getElementById('authValue').value;
	let checkbox = $('input[name=checkItem]:checked');
	let detailId = $('input[name=memberId]').val();
	let memberId = "";
	let arr = new Array();
	//1-1. 전체목록에서 권한변경
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			let td = tr.children();
			memberId = td.eq(2).text();
			arr.push(memberId);
		});
	}
	//1-2. 전체목록에서 권한변경 시도 중 선택된 체크박스가 없음
	if(memberId == "") {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 계정을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	//2. 상세정보에서 권한변경
	if(detailId) {
		memberId = detailId;
		console.log(detailId);
		arr.push(memberId);
	}
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

/* 계정정지 */
function suspendAcc() {
	let checkbox = $('input[name=checkItem]:checked');
	let detailId = $('input[name=memberId]').val();
	let memberId = "";
	let accSuspDesc = "";
	if(checkbox.length == 1 || detailId != null) {
		if(checkbox.length == 1) {
			let tr = checkbox.parent().parent().eq(0);
			let td = tr.children();
			memberId = td.eq(2).text();
			console.log(memberId);
		}
		
		if(detailId != null) {
			memberId = detailId;
			console.log(memberId);
		}
			
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

/* 계정정지해제 */
function activateAcc() {
	let checkbox = $('input[name=checkItem]:checked');
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

/* 새로고침 */
function refreshList() {
	window.location.assign('/admin/member/list');
}