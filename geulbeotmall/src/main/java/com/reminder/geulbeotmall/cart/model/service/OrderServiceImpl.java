package com.reminder.geulbeotmall.cart.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.geulbeotmall.cart.model.dao.OrderMapper;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	
	private final OrderMapper orderMapper;
	
	@Autowired
	public OrderServiceImpl(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}
	
	@Override
	public int addOrderInfo(OrderDTO orderDTO) {
		orderMapper.decreaseStockAmount(orderDTO.getOrderQuantity(), orderDTO.getOptionNo()); //재고수량 차감
		return orderMapper.addOrderInfo(orderDTO);
	}

	@Override
	public int addOrderDetail(OrderDetailDTO orderDetailDTO) {
		return orderMapper.addOrderDetail(orderDetailDTO);
	}

	@Override
	public int addPaymentInfo(PaymentDTO paymentDTO) {
		return orderMapper.addPaymentInfo(paymentDTO);
	}

	@Override
	public OptionDTO getWishItemByOptionNo(String username, int optionNo) {
		return orderMapper.getWishItemByOptionNo(username, optionNo);
	}
}
