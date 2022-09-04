/* 별점 */
$('.stars label').click(function(){
	let target = $(this);
	target.parent().children('label').css('color', '#E5E5E5');
	target.css('color', '#fc0').prevAll('label').css('color', '#fc0');
});

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
	console.log(index);
	if(index == 0) {
		fileItems[index].click();
	}
};

/* 리뷰등록 폼 제출 */
const urlParams = new URL(location.href).searchParams;
const orderNo = urlParams.get('order');
const optionNo = urlParams.get('option');
console.log(orderNo);
console.log(optionNo);
function submitWriteForm(form, event) {
	event.preventDefault();
	
	//양식 제출 전 필수 입력값 확인
	let text = '';
	if(!document.querySelector('input[name=revwTitle]').value.trim().length > 0) {
		text = '제목을 입력하세요';
		Swal.fire({
			icon: 'error',
			title: text,
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		})
		return;
	} else if(!document.querySelector('input[name=revwRatings]:checked')) {
		text = '별점을 선택하세요';
		Swal.fire({
			icon: 'error',
			title: text,
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		})
	} else if(!(document.querySelector('textarea[name=revwContent]').value.length >= 10)) {
		//console.log(document.querySelector('textarea[name=content]').value.length);
		text = '내용을 최소 10자 이상 입력하세요';
		Swal.fire({
			icon: 'error',
			title: text,
			confirmButtonColor: '#00008b',
			confirmButtonText: '확인'
		})
	}
	
	//FormData 객체 생성
	let formData = new FormData();
	let attached = $('.files');
	console.log(attached.length);
	for(let i=0; i < attached.length; i++) {
		if(attached[i].files.length > 0) {
			for(let j=0; j < attached[i].files.length; j++) {
				formData.append("files", $('.files')[i].files[j]);
			}
		}
	}
	
	$.ajax({
		url : '/review/write',
		data : formData,
		processData: false,
		contentType: false,
		enctype: 'multipart/form-data',
		type : 'post',
		traditional : true,
		success : function(data){
			if(data.errorMessage) {
				Swal.fire({
					icon: 'error',
					title: data.errorMessage,
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						return;
					}
				})
			}
			
			if(data.successMessage) {
				Swal.fire({
					icon: 'success',
					title: data.successMessage,
					confirmButtonColor: '#00008b',
					confirmButtonText: '확인'
				}).then((result) => {
					if(result.isConfirmed) {
						window.location.href='/mypage/main'; //마이페이지 메인으로 이동
						window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
};