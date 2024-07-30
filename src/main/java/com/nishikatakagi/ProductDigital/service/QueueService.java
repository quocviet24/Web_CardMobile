package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.model.Queue;

import java.util.List;

public interface QueueService {
    void saveQueueOrder(Queue queue);
    void deleteQueueOrder(int orderId);
    Queue findQueueByOrderId(int orderId);
    List<Queue> findAll();
}
