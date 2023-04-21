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

/* 판매여부관리 */
function manageSale() {
	let optValue = document.getElementById('saleValue').value;
	let checkbox = $('input[name=checkItem]:checked');
	let detailNo = $('input[name=prodNo]').val();
	let prodNo = "";
	let arr = new Array();
	//1-1. 전체목록에서 권한변경
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			let td = tr.children();
			prodNo = td.eq(1).text(); //td 태그 중 1번 index에 해당하는 text
			arr.push(prodNo);
		});
	}
	//1-2. 전체목록에서 판매여부변경 시도 중 선택된 체크박스가 없음
	if(prodNo == "") {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 상품을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	//2. 상세정보에서 판매여부변경
	if(detailNo) {
		prodNo = detailNo;
		console.log(detailNo);
		arr.push(prodNo);
	}
	console.log(optValue);
	console.log(arr);
	
	$.ajax({
		url : '/admin/product/manageSale',
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
					title: '판매여부가 변경되었습니다',
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

function deleteProduct() {
	let checkbox = $('input[name=checkItem]:checked');
	let prodNo = "";
	if(checkbox.length == 1) {
		let tr = checkbox.parent().parent().eq(0);
		let td = tr.children();
		prodNo = td.eq(1).text(); //td 태그 중 1번 index에 해당하는 text
		
		Swal.fire({
		icon: 'warning',
		title: '선택한 상품을 삭제하시겠습니까?',
		confirmButtonColor: '#00008b',
		confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				$.ajax({
					url : '/admin/product/deleteProduct',
					type : 'post',
					traditional : true,
					dataType : 'text',
					data : { no : prodNo },
					success : function(result){
						if(result == '성공') {
							Swal.fire({
								icon: 'success',
								title: '상품이 삭제되었습니다',
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
		})
	} else {
		Swal.fire({
			icon: 'warning',
			title: '1개의 상품을 체크하세요',
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
	window.location.assign('/admin/product/list');
}