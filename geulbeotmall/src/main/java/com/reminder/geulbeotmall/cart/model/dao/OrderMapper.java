package com.reminder.geulbeotmall.cart.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.geulbeotmall.cart.model.dto.DeliveryDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDTO;
import com.reminder.geulbeotmall.cart.model.dto.OrderDetailDTO;
import com.reminder.geulbeotmall.cart.model.dto.PaymentDTO;
import com.reminder.geulbeotmall.cart.model.dto.PointDTO;
import com.reminder.geulbeotmall.product.model.dto.OptionDTO;

@Mapper
public interface OrderMapper {

	int addOrderInfo(OrderDTO orderDTO);
	
	int decreaseStockAmount(int orderQuantity, int optionNo);

	int addOrderDetail(OrderDetailDTO orderDetailDTO);

	int addPaymentInfo(PaymentDTO paymentDTO);

	OptionDTO getWishItemByOptionNo(String username, int optionNo);

	int addDeliveryInfo(DeliveryDTO deliveryDTO);

	int addPointInfo(PointDTO pointDTO);

	int getTheNumberOfEachOrder(String orderNo);
}
