package com.reminder.geulbeotmall.cart.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.geulbeotmall.cart.model.dao.OrderMapper;
import com.reminder.geulbeotmall.cart.model.dto.DeliveryDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;

import net.sf.json.JSONArray;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	
	private final OrderMapper orderMapper;
	
	@Autowired
	public OrderServiceImpl(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	@Override
	public OptionDTO getWishItemByOptionNo(String username, int optionNo) {
		return orderMapper.getWishItemByOptionNo(username, optionNo);
	}
	
	@Transactional(rollbackFor={Exception.class, Error.class})
	@Override
	public boolean orderAndPay(OrderDetailDTO orderDetailDTO, JSONArray optionNoArr, JSONArray optionQtArr,
			JSONArray orderPriceArr, DeliveryDTO deliveryDTO, PointDTO pointDTO, PaymentDTO paymentDTO) {
		boolean result = false;
		
		int addOrderDetail = orderMapper.addOrderDetail(orderDetailDTO);
		
		int countAdd = 0;
		int countDecrease = 0;
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderNo(orderDetailDTO.getOrderNo());
		for(int i=0; i < optionNoArr.size(); i++) {
			int optionNo = Integer.parseInt(optionNoArr.getString(i));
			int optionQt = Integer.parseInt(optionQtArr.getString(i));
			String orderAmount = orderPriceArr.getString(i);
			orderDTO.setOptionNo(optionNo);
			orderDTO.setOrderQuantity(optionQt);
			orderDTO.setOrderAmount(Integer.parseInt(orderAmount));
			countAdd += orderMapper.addOrderInfo(orderDTO);
			countDecrease += orderMapper.decreaseStockAmount(orderDTO.getOrderQuantity(), orderDTO.getOptionNo()); //재고수량 차감
		}
		
		int addDeliveryInfo = orderMapper.addDeliveryInfo(deliveryDTO);
		
		int addPaymentInfo = orderMapper.addPaymentInfo(paymentDTO);
		
		int addPointInfo = orderMapper.addPointInfo(pointDTO);
		
		if(addOrderDetail == 1 && countAdd == optionNoArr.size() && countDecrease == optionNoArr.size()
				&& addDeliveryInfo == 1 && addPointInfo == 1 && addPaymentInfo ==1) {
			result = true;
		}
		return result;
	}

	@Override
	public int getTheNumberOfEachOrder(String orderNo) {
		return orderMapper.getTheNumberOfEachOrder(orderNo);
	}
}
