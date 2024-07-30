package com.nishikatakagi.ProductDigital.service_impl;

import com.nishikatakagi.ProductDigital.model.Queue;
import com.nishikatakagi.ProductDigital.repository.QueueOrderRepository;
import com.nishikatakagi.ProductDigital.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueueServiceImpl implements QueueService {
    @Autowired
    QueueOrderRepository queueOrderRepository;


    @Override
    public void saveQueueOrder(Queue queue) {
        queueOrderRepository.save(queue);
    }

    public void deleteQueueOrder(int orderId) {
        // Tìm Queue dựa trên orderId
        Optional<Queue> queueOptional = queueOrderRepository.findByOrderId(orderId);
        if (queueOptional.isPresent()) {
            Queue queue = queueOptional.get();
            queueOrderRepository.delete(queue);
        } else {
            throw new RuntimeException("Queue with orderId " + orderId + " not found");
        }
    }

    @Override
    public Queue findQueueByOrderId(int orderId) {
        Optional<Queue> queueOptional = queueOrderRepository.findByOrderId(orderId);
        return queueOptional.orElse(null);  // Returns null if the queue is not found
    }

    @Override
    public List<Queue> findAll() {
        return queueOrderRepository.findAll();
    }
}
