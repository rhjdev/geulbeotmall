/* 이미지 미리보기 */
let fileItems = document.querySelectorAll('[type=file]');
let imageBox = document.querySelectorAll('.imageBox');
fileItems.forEach(item => item.addEventListener('change', previewImage));

function previewImage() {
	let index = Array.from(fileItems).indexOf(this);
	if(this.files && this.files[0]) {
		let reader = new FileReader();
		reader.readAsDataURL(this.files[0]);
		reader.onload = function() {
			imageBox[index].innerHTML = "<img src='" + reader.result + "'>";
		}
	}
}

/* 새 카테고리 추가 */
function addACategory() {
let category = document.getElementById('categoryName').value;
	//새 카테고리 추가 클릭 시 입력란 활성화
	if(category == 'etc') {
		$('#addNewCategory').attr('disabled', false);
		$('#addNewCategory').css('background-color', 'transparent');
		
		$('#addNewCategory').blur(function(){
			//입력값을 카테고리에 대입
			category = document.getElementById('addNewCategory').value;
			console.log("addNewCategory : " + category);
			
			let param = { category : category };
		
			$.ajax({
				url : '/admin/product/checkCategory',
				data : JSON.stringify(param),
				type : 'post',
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data){
					//카테고리 중복인 경우에만 에러메시지 표시
					if(data.errorMessage) {
						Swal.fire({
							icon: 'error',
							title: data.errorMessage,
							confirmButtonColor: '#00008b',
							confirmButtonText: '확인'
						}).then((result) => {
							if(result.isConfirmed) {
								$('#addNewCategory').val(''); //입력값 삭제 및 입력란 비활성화
								$('#addNewCategory').attr('disabled', true);
								$('#addNewCategory').css('background-color', '#E5E5E5');
								$('#categoryName').val(category); //해당 카테고리 자동 선택
								return;
							}
						})
					}
				},
				error : function(status, error){ console.log(status, error); }
			});
			
		})
	//다른 값 선택 시 입력란 초기화 및 비활성화
	} else {
		$('#addNewCategory').val('');
		$('#addNewCategory').attr('disabled', true);
		$('#addNewCategory').css('background-color', '#E5E5E5');
	}
}

/* 새 브랜드 추가 */
function addABrand() {
	let brand = document.getElementById('brandName').value;
	//새 브랜드 추가 클릭 시 입력란 활성화
	if(brand == 'etc') {
		$('#addNewBrand').attr('disabled', false);
		$('#addNewBrand').css('background-color', 'transparent');
		
		$('#addNewBrand').blur(function(){
			//입력값을 브랜드에 대입
			brand = document.getElementById('addNewBrand').value;
			console.log("addNewBrand : " + brand);
			
			let param = { brand : brand };
		
			$.ajax({
				url : '/admin/product/checkBrand',
				data : JSON.stringify(param),
				type : 'post',
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data){
					//브랜드 중복인 경우에만 에러메시지 표시
					if(data.errorMessage) {
						Swal.fire({
							icon: 'error',
							title: data.errorMessage,
							confirmButtonColor: '#00008b',
							confirmButtonText: '확인'
						}).then((result) => {
							if(result.isConfirmed) {
								$('#addNewBrand').val(''); //입력값 삭제 및 입력란 비활성화
								$('#addNewBrand').attr('disabled', true);
								$('#addNewBrand').css('background-color', '#E5E5E5');
								$('#brandName').val(brand); //해당 브랜드 자동 선택
								return;
							}
						})
					}
				},
				error : function(status, error){ console.log(status, error); }
			});
			
		})
	//다른 값 선택 시 입력란 초기화 및 비활성화
	} else {
		$('#addNewBrand').val('');
		$('#addNewBrand').attr('disabled', true);
		$('#addNewBrand').css('background-color', '#E5E5E5');
	}
}

/* 태그 선택값 확인 */
let tagArr = "";
$('#tag').on('change', function(){
	tagArr = $('#tag').val().join('$');
	console.log(tagArr);
});

/* 옵션 추가 */
let bodyColor = new Array();
let inkColor = new Array();
let pointSize = new Array();
let stockAmount = new Array();
let extraCharge = new Array();
let optArr = new Array();
$('#addOpt').on('click', function(){
	bodyColor.push($('#bodyColor option:checked').text());
	inkColor.push($('#inkColor option:checked').text());
	pointSize.push($('#pointSize option:checked').val());
	stockAmount.push($('#amount').val());
	extraCharge.push($('#extraCharge').val());
	
	optArr.push({bodyColor : $('#bodyColor option:checked').text(),
				 inkColor : $('#inkColor option:checked').text(),
				 pointSize : $('#pointSize option:checked').val(),
				 stockAmount : $('#amount').val(),
				 extraCharge : $('#extraCharge').val()}
	);
	
	//상단의 추가된 옵션 목록으로 반영
	for(let i=0; i < optArr.length; i++) {
		if([i] > 0) { //첫번째 옵션 이후로 추가가 이어질 경우
			$('#addedOption' + [i]).parent().parent().attr('hidden', false); //ul 태그 hidden 속성 비활성화
		}
		
		if([i] >= 10) { //0부터 시작한 index, 최대 10개까지만 수용
			Swal.fire({
				icon: 'warning',
				title: '옵션 추가는 최대 10개입니다',
				confirmButtonColor: '#00008b',
				confirmButtonText: '확인'
			}).then((result) => {
				if(result.isConfirmed) {
					return;
				}
			})
		}
		$('#addedOption' + [i]).val("바디컬러_" + optArr[i].bodyColor + " | 잉크컬러_" + optArr[i].inkColor + " | 심두께및스펙_" + optArr[i].pointSize + " | 추가금액_" + optArr[i].extraCharge + "원(" + optArr[i].stockAmount + "개)");
	}
});

/* 옵션 초기화 */
$('#deleteOpt').on('click', function(){
	for(let i=0; i < 10; i++) {
		let index = $('#addedOption' + [i]).val();
		if(index) { //추가된 옵션이 존재하는 경우
			document.getElementById('addedOption' + [i]).value = ''; //값 초기화
			optArr = []; //저장된 배열 초기화
			if(i != 0) {
				$('#addedOption' + [i]).parent().parent().attr('hidden', true); //첫번째 이후의 옵션은 다시 hidden
			}
		}
	}
});

function submitProductForm() {
	
	//카테고리
	let category = document.getElementById('categoryName').value;
	if(category == 'etc') {
		category = document.getElementById('addNewCategory').value;
	}
	console.log(category);
	
	//상품명
	let prodName = document.getElementById('prodName').value;
	console.log(prodName);
	
	//상품설명
	let prodDesc = document.getElementById('prodDesc').value;
	console.log(prodDesc);
	
	//주요특징태그
	console.log(tagArr);
	
	//할인율
	let discountRate = document.getElementById('discount').value;
	console.log(discountRate);
	
	//원가
	let prodPrice = document.getElementById('prodPrice').value;
	console.log(prodPrice);
	
	//브랜드
	let brand = document.getElementById('brandName').value;
	if(brand == 'etc') {
		brand = document.getElementById('addNewBrand').value;
	}
	console.log(brand);
	
	//원산지
	let prodOrigin = document.getElementById('prodOrigin').value;
	console.log(prodOrigin);
	
	//추가된옵션
	console.log(optArr);
	
	//상세내용
	let prodDetailContent = CKEDITOR.instances['prodDetailContent'].getData();
	console.log(prodDetailContent);
	
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
	
	let params = { category : category
				 , prodName : prodName
				 , prodDesc : prodDesc
				 , tagArr : tagArr
				 , discountRate : discountRate
				 , prodPrice : prodPrice
				 , brand : brand
				 , prodOrigin : prodOrigin
				 , prodDetailContent : prodDetailContent
				 , bodyColor : bodyColor
				 , inkColor : inkColor
				 , pointSize : pointSize
				 , stockAmount : stockAmount
				 , extraCharge : extraCharge
				 , optArrLength : optArr.length
				 };
	
	formData.append("params", new Blob([JSON.stringify(params)], {type : 'application/json'}));
	
	$.ajax({
		url : '/admin/product/add',
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
						window.location.reload(); //페이지 새로고침
						window.history.scrollRestoration = 'manual'; //스크롤 최상단 고정
					}
				})
			}
		},
		error : function(status, error){ console.log(status, error); }
	});
}