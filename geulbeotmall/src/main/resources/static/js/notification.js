/* SSE 통신(server to client) */
$(document).ready(function(){
	console.log(username); //default layout 통해 session 정보 호출
	
	const ul = document.getElementById('notification-list');
	const list = document.createDocumentFragment();
	let notifications;
	let count;
	if(username != null) {
		const eventSource = new EventSource('/subscribe');
		eventSource.addEventListener('sse', function(e) {
			const data = JSON.parse(e.data);
			console.log(e.data);
			console.log(e.lastEventId); //notification id
			
				
			//DB 연동된 전체 알림 조회
			fetch('/notification/' + username)
				.then((response) => response.json())
				.then((data) => {
					console.log(data);
					
					notifications = data;
					count = `${notifications.length}`; //총 알림 개수
					console.log(count);
					
					if(count > 0) { //데이터가 있을 때만 알림버튼 노출
						document.getElementById('buttonNotification').style.display = 'block'; //알림버튼 노출
					}
					
					notifications.map(function(notification) {
						//notification data
						let notificationId = `${notification.notificationId}`;
						let receiver = `${notification.receiver}`;
						let content = `${notification.content}`;
						let notificationType = `${notification.notificationType}`;
						let url = `${notification.url}`;
						let readYn = `${notification.readYn}`;
						let deletedYn = `${notification.deletedYn}`;
						let createdAt = `${notification.createdAt}`;
						let updatedAt = `${notification.updatedAt}`;
						
						//count 반영
						let b = document.querySelector('.notificationCntText');
						let bell = document.querySelector('.notificationCnt');
						b.innerHTML = count;
						bell.innerHTML = count;
						
						//content 반영
						let item = document.createElement('li');
						let label = document.createElement('label');
						let text = document.createElement('span');
						let date = document.createElement('small');
						let button = document.createElement('span');
						let icon = document.createElement('i');
						item.className = 'notification-item';
						label.className = 'notification-item-label';
						text.className = 'notification-content';
						date.className = 'notification-created';
						button.className = 'deleteNotificationBtn';
						icon.className = 'fa-regular fa-trash-can';
						date.innerHTML = `${notification.createdAt}`;
						text.innerHTML = `${notification.content}`;
						
						button.appendChild(icon);
						text.appendChild(date);
						label.appendChild(text);
						item.appendChild(button);
						item.appendChild(label);
						list.appendChild(item);
						ul.appendChild(list);
						
						//읽음 상태 일괄 업데이트
						fetch('/notification/' + username + '/read', {
							method: 'PUT',
							headers: { 'Content-Type' : 'application/json' }
						}).then((response) => response.json())
						  .then((data) => console.log(data));
						
						//단일 알림 클릭 시 해당 페이지로 이동
						text.addEventListener('click', function(){
							window.location.href = `${notification.url}`;
						});
						
						//삭제 버튼 클릭 시 해당 알림만 삭제 상태 업데이트
						button.addEventListener('click', function(){
							fetch('/notification/' + `${notification.notificationId}` + '/delete', {
								method: 'PUT',
								headers: { 'Content-Type' : 'application/json' }
							}).then((response) => response.json())
							  .then((data) => console.log(data));
						});
					});
				}).catch(function(error) {
					console.log(error);
			});
			
			/*
			(async() => {
				const showNotification = () => {
					const notification = new Notification('작성하신 게시글에 댓글이 달렸습니다', {
						body: data.content
					});
					setTimeout(() => {
						notification.close();
					}, 10 * 1000);
					notification.addEventListener('click', () => {
						window.open(data.url, '_blank');
					});
				}
				
				let granted = false; //브라우저 알림 허용 권한
				if(Notification.permission === 'granted') {
					granted = true;
				} else if(Notification.permission != 'denied') {
					let permission = await Notification.requestPermission();
					granted = permission === 'granted';
				}
				
				if(granted) showNotification(); //권한이 주어진 경우에 한하여 알림
			})();
			*/
		});
	}
})

/* 알링창 여닫기 */
document.getElementById('buttonNotification').addEventListener('click', function(){
	let currentStyle = document.querySelector('.notification-container').style.display;
	if(currentStyle === 'none') {
		//아이콘 클릭 시 알림창 열기
		$(this).closest('div').find('.notification-container').attr('style', 'display: block');
	} else {
		//닫기 버튼 클릭 시 알림창 닫기
		$(this).closest('div').find('.notification-container').attr('style', 'display: none');
	}
});