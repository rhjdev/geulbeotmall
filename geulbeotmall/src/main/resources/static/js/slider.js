/* Initialize Swiper */
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