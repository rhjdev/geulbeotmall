/* 관리자페이지 회원목록 검색어 유무 확인 */
function checkKeywordForMember() {
	let form = document.querySelector('#searchForm');
	let keyword = document.querySelector('input[name=keyword]').value;
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
	} else {
		form.submit();
	}
}

/* 관리자페이지 게시글목록 검색어 유무 확인 */
function checkKeywordForPost() {
	let form = document.querySelector('#searchForm');
	let keyword = document.querySelector('input[name=keyword]').value;
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				window.location.assign('/admin/post/list');
			}
		})
	} else {
		form.submit();
	}
}

/* 관리자페이지 상품목록 검색어 유무 확인 */
function checkKeywordForProduct() {
	let form = document.querySelector('#searchForm');
	let keyword = document.querySelector('input[name=keyword]').value;
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
	} else {
		form.submit();
	}
}

/* 관리자페이지 주문/배송목록 검색어 유무 확인 */
function checkKeywordForOrder() {
	let form = document.querySelector('#searchForm');
	let keyword = document.querySelector('input[name=keyword]').value;
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
	} else {
		form.submit();
	}
}

/* 헤더 상품 검색 */
function checkKeywordToSearch() {
	let form = document.querySelector('#searchForm');
	let keyword = document.querySelector('input[name=keyword]').value;
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
	} else {
		form.submit();
	}
}

/* 고객센터 게시글/댓글 검색 */
function checkKeywordForInquiry() {
	let form = document.querySelector('#searchPostCommentForm');
	let keyword = $('#searchPostCommentForm').find('input[name=keyword]').val();
	console.log(keyword);
	if(keyword.length == 0) {
		Swal.fire({
			icon: 'warning',
			title: '검색어를 입력하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		}).then((result) => {
			if(result.isConfirmed) {
				window.location.assign('/cs/main');
			}
		})
	} else {
		form.submit();
	}
}