/* 1:1 문의 폼 제출 */
/* A. '문의 유형' 선택 시 제목,내용 입력란 활성화 */
function selectInquiryType() {
	let type = document.getElementById('inquiryType');
	let value = type.options[type.selectedIndex].value;
	//console.log(value.indexOf('선택'));
	if(value.indexOf('선택') == -1) {
		$('#inquiryTitle').attr('disabled', false);
		$('#inquiryContent').attr('disabled', false); //기본 textarea
		CKEDITOR.instances['inquiryContent'].setReadOnly(false);
	} else {
		$('#inquiryTitle').attr('disabled', true);
		$('#inquiryContent').attr('disabled', true); //기본 textarea
		CKEDITOR.instances['inquiryContent'].setReadOnly(true);
	}
}

/* B. 필수 입력값 확인 후 제출 */
function submitInquiryForm(form, event) {
	event.preventDefault();
		
	if(!($('#inquiryTitle').val().trim().length > 0 && $('#inquiryContent').val().trim().length > 0)) {
		Swal.fire({
			icon: 'error',
			title: '필수 입력 항목을 작성하세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		})
	} else if(!$('#agreement').prop('checked')) {
		Swal.fire({
			icon: 'error',
			title: '개인정보 수집 및 이용에 동의해 주세요',
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		})
	} else {
		form.submit();
	}
}

function confirmToDelete() {
	console.log(inquiryNo);
	Swal.fire({
		icon: 'warning',
		title: '문의를 삭제할까요?',
		showCancelButton: true,
		confirmButtonColor: '#00008b',
		confirmButtonText: '삭제',
		cancelButtonColor: '#6c757d',
		cancelButtonText: '취소',
		reverseButtons: true
	}).then((result) => {
		if(result.isConfirmed) {
			$.ajax({
				url : '/cs/inquiry/delete',
				type : 'get',
				data : { no : inquiryNo },
				success : function(){
					Swal.fire({
						icon: 'success',
						title: '게시글 삭제가 완료되었습니다',
						confirmButtonColor: '#00008b',
						confirmButtonText: '확인'
					}).then((result) => {
						if(result.isConfirmed) {
							window.location.assign('/cs/main');
						}
					});
				},
				error : function(status, error){ console.log(status, error); }
			});
		}
	});
}

/* 썸네일 미리보기 및 클릭 시 재선택 */
let fileItems = document.querySelectorAll('[type=file]');
let imageItem = document.querySelectorAll('.imageItem');
let imageBox = document.querySelectorAll('.imageBox');
fileItems.forEach(item => item.addEventListener('change', previewImage));

function previewImage() {
	let index = Array.from(fileItems).indexOf(this); //fileItems 기준으로 index 생성
	//console.log(index);
	if(this.files && this.files[0]) {
		let reader = new FileReader();
		reader.readAsDataURL(this.files[0]);
		reader.onload = function() {
			imageBox[index].innerHTML = "<img src='" + reader.result + "'>";
		}
	} else {
		imageItem.forEach(item => item.addEventListener('click', reselectImage));
	}
};

function reselectImage() {
	let index = Array.from(imageItem).indexOf(this); //imageItem 기준으로 index 생성
	//console.log(index);
	if(index == 0) {
		fileItems[index].click();
	}
};