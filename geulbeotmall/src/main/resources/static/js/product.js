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

/* 태그 선택값 확인 */
$('#tag').on('change', function(){
	let tagArr = { value : $('#tag').val() };
	console.log(tagArr);
});

/* 옵션 추가 */
function addProductOption() {
	let param = { bodyColor : $('#bodyColor option:checked').text(),
				  inkColor : $('#inkColor option:checked').text(),
				  pointSize : $('#pointSize option:checked').val(),
				  stockAmount : $('#amount').val(),
				  extraCharge : $('#extraCharge').val()
	};
	console.log(param);
	
	$.ajax({
		url : '/admin/product/addOption',
		data : JSON.stringify(param),
		type : 'post',
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
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
		},
		error : function(status, error){ console.log(status, error); }
	});
}

function submitProductForm() {
	//카테고리
	let category = document.getElementById('categoryName').value;
	console.log("categoryName : " + category);
	if(category == 'etc') {
		category = document.getElementById('addNewCategory').value;
		console.log("addNewCategory : " + category);
	}
	console.log(category);
	
	//태그
	let tag = document.getElementById('tag').value;
	console.log(tag);
	
}
