/* 탭 활성화 컨트롤 : html에서 'active' 정의하지 않고 이곳에서 관리 */
$(document).ready(function(){
	let tabs = document.querySelectorAll('button.nav-link');
	let panels = document.querySelectorAll('div.tab-pane');
	
	let activeTab = localStorage.getItem('activeTab') == 'undefined' ? 'total-tab' : localStorage.getItem('activeTab'); //기본 active tab=inquiry-tab
	//console.log(localStorage.getItem('activeTab'));
	
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

/* 배송상태관리 */
function manageDeliveryStatus() {
	let dlvrValue = document.getElementById('dlvrValue').value;
	let checkbox = $('input[name=checkItem]:checked');
	let detailNo = $('input[name=orderNo]').val();
	let orderNo = "";
	let arr = new Array();
	//1-1. 전체목록에서 상태변경
	if(checkbox) {
		checkbox.each(function(i){
			let tr = checkbox.parent().parent().eq(i);
			let td = tr.children();
			console.log(tr);
			orderNo = td.eq(7)[0].attributes.value.textContent; //td 태그 중 n번 index에 해당하는 value
			console.log(orderNo);
			arr.push(orderNo);
		});
	}
	//1-2. 전체목록에서 배송상태변경 시도 중 선택된 체크박스가 없음
	if(orderNo == "") {
		Swal.fire({
			icon: 'warning',
			title: '1개 이상의 주문을 체크하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
	}
	//2. 상세정보에서 배송상태변경
	if(detailNo) {
		orderNo = detailNo;
		console.log(detailNo);
		arr.push(orderNo);
	}
	console.log(dlvrValue);
	console.log(arr);
	
	$.ajax({
		url : '/admin/order/manageDeliveryStatus',
		type : 'post',
		traditional : true, //배열 넘기기 위한 세팅
		dataType : 'text',
		data : {
			dlvrValue : dlvrValue,
			arr : arr
		},
		success : function(result){
			if(result == 'succeed'){
				Swal.fire({
					icon: 'success',
					title: '배송상태가 변경되었습니다',
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

/* 새로고침 */
function refreshList() {
	window.location.assign('/admin/order/list');
}