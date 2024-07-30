package com.nishikatakagi.ProductDigital.service_impl;

import com.nishikatakagi.ProductDigital.model.OrderDetail;
import com.nishikatakagi.ProductDigital.repository.OrderDetailRepository;
import com.nishikatakagi.ProductDigital.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailImpl implements OrderDetailService {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> findListOrderDetailByOrderId(int orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
