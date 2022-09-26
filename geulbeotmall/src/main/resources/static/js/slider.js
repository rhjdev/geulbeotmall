/* Initialize Swiper */
let link = window.location.href;
if(link.indexOf('design') == -1) { //main 페이지 한정 동작
	$(document).ready(function(){
		var swiper = new Swiper('.mainSwiper', {
			autoplay: {
				delay: 5000,
				disableOnInteraction: false
			},
			speed: 500,
			effect: 'fade',
			loop: 'infinite',
			mousewheel: true,
			direction: "vertical",
			pagination: {
				el: '.swiper-pagination',
				dynamicBullets: true,
				clickable: true
	        },
		});
	});
}

/* 썸네일 미리보기 및 클릭 시 재선택 */
let fileItems = document.querySelectorAll('[type=file]');
let imageItem = document.querySelectorAll('.imageItem');
let imageBox = document.querySelectorAll('.imageBox');
fileItems.forEach(item => item.addEventListener('change', previewImage));
console.log(fileItems);
function previewImage() {
	let index = Array.from(fileItems).indexOf(this); //fileItems 기준으로 index 생성
	//console.log(imageItem);
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
	if(index == 0) {
		fileItems[index].click();
	}
};

function submitImageForm() {
	event.preventDefault();
	
	let refArr = new Array();
	let target = event.target.className;
	/* 슬라이더 추가 */
	if(target.indexOf('addSliderBtn') >= 0) {
		let form = document.getElementById('addSliderForm');
		let input = document.querySelectorAll('input[class=slider-input]');
		
		for(let i=1; i < 10; i++) { //9개의 슬라이드 모두 탐색
			let slide = document.querySelector('#refProdNo' + i + ' option:checked');
			if(slide != null) {
				let prodNo = slide.value;
				console.log(prodNo);
				refArr.push(prodNo);
			}
		}
		let refSlider = document.getElementById('refSlider');
		refSlider.value = refArr; //슬라이드별 상품번호 제출용 데이터로 저장
		
		for(let i=0; i < 9; i++) {
			let attach = imageBox[i].innerHTML;
			if(!attach.includes('img')) { //첨부된 슬라이드가 없는 경우 제출에서 제외
				console.log(i);
				document.querySelector('input[id=image' + (i+1) + ']').setAttribute('disabled', 'disabled');
			}
		}
		form.submit();
	/* 배너 추가 */
	} else {
		let form = document.getElementById('addBannerForm');
		let input = document.querySelector('input[class=banner-input]');
		
		let banner = imageBox[9].innerHTML;
		if(banner.includes('img')) { //첨부된 banner img 파일이 존재하는 경우
			refArr.push('banner');
			form.submit();
		}
	}
}