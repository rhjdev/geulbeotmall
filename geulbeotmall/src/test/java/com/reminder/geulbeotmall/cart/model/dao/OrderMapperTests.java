package com.reminder.geulbeotmall.cart.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.reminder.geulbeotmall.cart.model.dto.DeliveryDTO;
import com.reminder.geulbeotmall.cart.model.dto.DeliveryRequest;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.config.GeulbeotmallApplication;
import com.reminder.geulbeotmall.member.model.dao.MemberMapper;
import com.reminder.geulbeotmall.member.model.dto.MemberDTO;
import com.reminder.geulbeotmall.product.model.dao.ProductMapper;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {GeulbeotmallApplication.class})
public class OrderMapperTests {

	private final OrderMapper orderMapper;
	private final MemberMapper memberMapper;
	private final ProductMapper productMapper;
	
	@Autowired
	public OrderMapperTests(OrderMapper orderMapper, MemberMapper memberMapper, ProductMapper productMapper) {
		this.orderMapper = orderMapper;
		this.memberMapper = memberMapper;
		this.productMapper = productMapper;
	}
	
	@Test
	@DisplayName("매퍼 인터페이스 의존성 주입 테스트")
	public void testInit() {
		assertNotNull(orderMapper);
		assertNotNull(memberMapper);
		assertNotNull(productMapper);
	}
	
	@Test
	@DisplayName("주문 성공 매퍼 테스트")
	public void testOrder() {
		//given
		for(int i=0; i < 12; i++) { //n개 dummy data 생성
			int number = new Random().nextInt(1, 10); //랜덤 주문 개수
			MemberDTO member = memberMapper.getRandomMember(); //랜덤 회원
			OptionDTO option = productMapper.getRandomProductOption(number); //랜덤 상품 옵션
			
			String memberId = member.getMemberId();
			String rcvrName = member.getName();
			String rcvrPhone = member.getPhone();
			String str[] = member.getAddress().split("\\$");
			log.info("str : {}", str[0]);
			String rcvrPostalCode = str[0];
			String rcvrAddress = str[1] + " " + str[2];
			
			int optionNo = option.getOptionNo();
			int optionQt = number;
			int orderAmount = option.getProduct().getProdPrice() * optionQt;
			
			String dlvrReqMessage = DeliveryRequest.values()[(int) (Math.random()*DeliveryRequest.values().length)].getLabel(); //enum에서 랜덤 추출
			int deliveryFee = 0;
			int pointAmount = 0;
			String paymentNo = "P" + new Date().getTime() + memberId.toUpperCase();
			String paymentMethod = "card";
			int paymentAmount = orderAmount + deliveryFee - pointAmount;
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a hh:mm:ss"); //시간 단위 1-12 한정(hh)
			Calendar cal = Calendar.getInstance();
			cal.set(2023, 6, 1); //month+1이므로 0~11
			String orderDate = simpleDateFormat.format(cal.getTime());
			String paymentDate = orderDate;
			Date dispatchDate = new Date();
			Date deliveryDate = new Date();
			
			/* 1. 주문정보 DB 저장 및 재고수량 차감 반영 */
			String orderNo = "ORD" + new Date().getTime() + memberId.toUpperCase();
			
			OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
			orderDetailDTO.setOrderNo(orderNo);
			orderDetailDTO.setMemberId(memberId);
			orderDetailDTO.setOrderDate(orderDate);
			orderDetailDTO.setRcvrName(rcvrName);
			orderDetailDTO.setRcvrPhone(rcvrPhone);
			orderDetailDTO.setRcvrAddress(rcvrPostalCode + " " + rcvrAddress);
			orderDetailDTO.setDlvrReqMessage(dlvrReqMessage);
			
			/* 2. 배송정보 DB 저장 */
			DeliveryDTO deliveryDTO = new DeliveryDTO();
			deliveryDTO.setOrderNo(orderNo);
			deliveryDTO.setDeliveryFee(deliveryFee);
			deliveryDTO.setDeliveryCompany("업체배송");
			deliveryDTO.setDispatchDate(null);
			deliveryDTO.setDeliveryDate(null);
			
			/* 3. 적립금 사용정보 DB 저장 */
			PointDTO pointDTO = new PointDTO();
			pointDTO.setPaymentNo(paymentNo);
			pointDTO.setPointAmount(pointAmount);
			pointDTO.setPointDateTime(paymentDate);
			pointDTO.setPointStatus("사용");
			
			/* 4. 결제정보 DB 저장 */
			PaymentDTO paymentDTO = new PaymentDTO();
			paymentDTO.setPaymentNo(paymentNo);
			paymentDTO.setOrderNo(orderNo);
			paymentDTO.setPaymentMethod(paymentMethod);
			paymentDTO.setPaymentAmount(paymentAmount);
			paymentDTO.setPaymentDateTime(paymentDate);
			
			/* 5. 신규/완료 구분 */
			/* 5-1. 신규 주문 */
			//orderDetailDTO.setDlvrStatus("상품준비중");
			/* 5-2. 완료 주문 */
			orderDetailDTO.setDlvrStatus("배송완료");
			cal.add(Calendar.DATE, 1); //주문일 기준 1일 뒤 출고
			dispatchDate = cal.getTime();
			deliveryDTO.setDispatchDate(dispatchDate);
			cal.add(Calendar.DATE, (int) (Math.random()*3)+1); //출고일 기준 2-3일 뒤 배송완료
			deliveryDate = cal.getTime();
			deliveryDTO.setDeliveryDate(deliveryDate);
			
			//when
			boolean result = false;
			
			int addOrderDetail = orderMapper.addOrderDetail(orderDetailDTO);
			
			int countAdd = 0;
			int countDecrease = 0;
			OrderDTO orderDTO = new OrderDTO();
			orderDTO.setOrderNo(orderDetailDTO.getOrderNo());
			orderDTO.setOptionNo(optionNo);
			orderDTO.setOrderQuantity(optionQt);
			orderDTO.setOrderAmount(orderAmount);
			countAdd += orderMapper.addOrderInfo(orderDTO);
			countDecrease += orderMapper.decreaseStockAmount(orderDTO.getOrderQuantity(), orderDTO.getOptionNo()); //재고수량 차감
			
			int addDeliveryInfo = orderMapper.addDeliveryInfo(deliveryDTO);
			
			int addPaymentInfo = orderMapper.addPaymentInfo(paymentDTO);
			
			int addPointInfo = orderMapper.addPointInfo(pointDTO);
			
			if(addOrderDetail == 1 && countAdd == 1 && countDecrease == 1
					&& addDeliveryInfo == 1 && addPointInfo == 1 && addPaymentInfo == 1) {
				result = true;
			}
			
			//then
			assertEquals(true, result);
		}
	}
}
