/* 관리자페이지 회원목록 검색어 유무 확인 */
function checkKeywordForMember() {
	let keyword = $('input[name=keyword]').val();
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				window.location.assign('/admin/member/list');
			}
		})
		e.preventDefault(); //페이지 이동 이벤트 막기
	}
}

/* 관리자페이지 상품목록 검색어 유무 확인 */
function checkKeywordForProduct() {
	let keyword = $('input[name=keyword]').val();
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				window.location.assign('/admin/product/list');
			}
		})
		e.preventDefault(); //페이지 이동 이벤트 막기
	}
}

/* 관리자페이지 주문/배송목록 검색어 유무 확인 */
function checkKeywordForOrder() {
	let keyword = $('input[name=keyword]').val();
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				window.location.assign('/admin/order/list');
			}
		})
		e.preventDefault(); //페이지 이동 이벤트 막기
	}
}

/* 상품 검색 */
function checkKeywordToSearch() {
	let keyword = $('input[name=keyword]').val();
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				history.go(0);
			}
		})
		e.preventDefault(); //페이지 이동 이벤트 막기
	}
}