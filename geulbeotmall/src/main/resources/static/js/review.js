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
	//console.log(index);
	if(index == 0) {
		fileItems[index].click();
	}
};