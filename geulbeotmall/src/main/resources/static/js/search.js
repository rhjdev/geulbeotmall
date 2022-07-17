/* 회원목록 검색어 유무 확인 */
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

/* 상품목록 검색어 유무 확인 */
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