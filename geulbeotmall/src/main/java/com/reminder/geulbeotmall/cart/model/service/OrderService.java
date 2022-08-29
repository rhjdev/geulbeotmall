package com.reminder.geulbeotmall.cart.model.service;

import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;

public interface OrderService {

	int addOrderInfo(OrderDTO orderDTO);

	int addOrderDetail(OrderDetailDTO orderDetailDTO);

	int addPaymentInfo(PaymentDTO paymentDTO);

	OptionDTO getWishItemByOptionNo(String username, int optionNo);
}
