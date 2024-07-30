package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> findListOrderDetailByOrderId(int orderId);
}
