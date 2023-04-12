let buttonClicked;
let form;
let commentNo;
/* 대댓글 작성 */
for(const item of document.querySelectorAll('.nestCommentBtn')) {
	item.addEventListener('click', function(event){
		buttonClicked = event.target;
		commentNo = event.target.parentElement.parentElement.children[2].attributes.value.value; //.comment-content value
		//console.log(buttonClicked);
		//console.log(commentNo);
		
		/* 대댓글 작성 폼 불러오기
		   display: none -> block
		 */
		form = $(this).closest('.commentBox').find('.comment-editor').find('#nestCommentForm' + commentNo);
		form.attr('style', 'display: block');
	});
}

/* 댓글 수정 */
for(const item of document.querySelectorAll('.editCommentBtn')) {
	item.addEventListener('click', function(event){
		buttonClicked = event.target;
		commentNo = event.target.parentElement.parentElement.children[2].attributes.value.value; //.comment-content value
		//console.log(buttonClicked);
		//console.log(commentNo);
		
		/* 댓글 수정 폼 불러오기
		   A. display: none -> block
		   B. 기존 내용 반영
		 */
		form = $(this).closest('.commentBox').find('.comment-editor').find('#editCommentForm' + commentNo);
		form.attr('style', 'display: block');
		let textarea = form[0][3];
		let origValue = event.target.parentElement.parentElement.children[2].children[0].innerHTML;
		textarea.value = origValue;
		//console.log(origValue);
		//console.log(textarea);
	});
}

/* 댓글 삭제 */
for(const item of document.querySelectorAll('.deleteCommentBtn')) {
	item.addEventListener('click', function(event){
		buttonClicked = event.target;
		console.log(buttonClicked);
		commentNo = event.target.parentElement.parentElement.children[2].attributes.value.value; //.comment-content value
		console.log(commentNo);
		
		Swal.fire({
			icon: 'warning',
			title: '댓글을 삭제하시겠습니까?',
			showCancelButton: true,
			confirmButtonColor: '#00008b',
			confirmButtonText: '삭제',
			cancelButtonColor: '#6c757d',
			cancelButtonText: '취소',
			reverseButtons: true
		}).then((result) =>{
			if(result.isConfirmed) {
				$.ajax({
					url : '/comment/delete',
					type : 'get',
					data : { 'commentNo' : commentNo },
					success : function(result) {
						location.reload(); //페이지 새로고침
					},
					error : function(status, error){ console.log(status, error); }
				});
			}
		});
	});
}