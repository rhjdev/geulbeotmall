package com.reminder.geulbeotmall.cart.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;

@Mapper
public interface OrderMapper {

	int addOrderInfo(OrderDTO orderDTO);
	
	void decreaseStockAmount(int orderQuantity, int optionNo);

	int addOrderDetail(OrderDetailDTO orderDetailDTO);

	int addPaymentInfo(PaymentDTO paymentDTO);
}
