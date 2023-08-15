/* 탭 활성화 컨트롤 : html에서 'active' 정의하지 않고 이곳에서 관리 */
$(document).ready(function(){
	let tabs = document.querySelectorAll('button.nav-link');
	let panels = document.querySelectorAll('div.tab-pane');
	
	let activeTab = localStorage.getItem('activeTab') == 'undefined' ? 'inquiry-tab' : localStorage.getItem('activeTab'); //기본 active tab=inquiry-tab
	if(localStorage.getItem('activeTab') == 'total-tab') activeTab = 'inquiry-tab';
	
	for(let i=0; i < tabs.length; i++) {
		if(tabs[i].id == activeTab) { tabs[i].classList.add('active'); } //선택 tab 활성화
	}
	for(let i=0; i < panels.length; i++) {
		if(panels[i].attributes['aria-labelledby'].value == activeTab) { panels[i].classList.add('active'); } //aria-labelledby="inquiry-tab"
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

/* 삭제글 복구 */
function restorePost() {
	let checkbox = $('div.tab-pane.active > div.dataTable-container > table > tbody > tr > td > input[name=checkItem]:checked');
	let trashNo = "";
	let arr = new Array();
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			trashNo = tr.find('td:first-child')[0].attributes['data-value'].value; //th:attr="data-value=${ value }"
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
	console.log(admin); //@SessionAttributes에서 넘어온 접속자 정보
	let checkbox = $('div.tab-pane.active > div.dataTable-container > table > tbody > tr > td > input[name=checkItem]:checked');
	let postNo = "";
	let memberId = "";
	let board = "";
	let noArr = new Array();
	let writerArr = new Array();
	let boardArr = new Array();
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			postNo = tr.find('td:first-child')[0].attributes['data-value'].value; //th:attr="data-value=${ value }"
			memberId = tr.children().eq(3)[0].attributes.value.nodeValue;
			//console.log(memberId);
			board = document.querySelector('.tab-pane.active').id;
			if(postNo != '') noArr.push(postNo);
			if(memberId != '') writerArr.push(memberId);
			if(board != '') boardArr.push(board);
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
			writerArr : writerArr,
			boardArr : boardArr,
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
	let checkbox = $('div.tab-pane.active > div.dataTable-container > table > tbody > tr > td > input[name=checkItem]:checked');
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