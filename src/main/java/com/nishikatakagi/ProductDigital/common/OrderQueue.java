package com.nishikatakagi.ProductDigital.common;

import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.service.OrderService;
import com.nishikatakagi.ProductDigital.service.QueueService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class OrderQueue {
    private static final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();

    @Autowired
    OrderService orderService;

    @Autowired
    QueueService queueService;

    // Hàm push để thêm đơn hàng vào hàng đợi và lưu vào cơ sở dữ liệu
    public void push(Order order) {
        orderQueue.add(order);
    }

    @PostConstruct
    public void startProcessing() {
        System.out.println("Initializing order processing thread...");

        Thread processorThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Waiting for orders...");
                    Order order = orderQueue.take();  // Lấy và loại bỏ phần tử đầu tiên trong hàng đợi, chờ nếu hàng đợi trống
                    System.out.println("Processing order: " );
                    String status = orderService.createOrderDetail(order);
                    // xóa đơn hàng đã thành công trong queue
                    queueService.deleteQueueOrder(order.getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        processorThread.setDaemon(true);  // Đặt thread là daemon để không ngăn chặn JVM shutdown
        processorThread.start();
        System.out.println("Order processing thread started.");
    }


}
