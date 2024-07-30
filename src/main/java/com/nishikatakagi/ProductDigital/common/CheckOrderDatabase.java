package com.nishikatakagi.ProductDigital.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.lib.payos.PayOS;
import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.Queue;
import com.nishikatakagi.ProductDigital.model.Transaction;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.OrderService;
import com.nishikatakagi.ProductDigital.service.QueueService;
import com.nishikatakagi.ProductDigital.service.TransactionService;
import com.nishikatakagi.ProductDigital.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckOrderDatabase {
    private final PayOS payOS;

    @Autowired
    QueueService queueService;

    @Autowired
    OrderService orderService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserService userService;

    public CheckOrderDatabase(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @PostConstruct
    public void ScanData(){
        List<Queue> listTracsactionInDataBase = queueService.findAll();
        for(Queue transaction : listTracsactionInDataBase){
            try {
                // Lấy thông tin thanh toán dạng json của mã đơn hàng trên payOS
                JsonNode paymentInfo = payOS.getPaymentLinkInfomation(transaction.getOrderTransaction());
                // Lấy trạng thái thanh toán của mã đơn hàng đó từ dạng json sang string status
                String status = ConvertJson.JsonToTransactionConverter.getStatusTransaction(paymentInfo);
                // Lấy đơn hàng tương ứng với giao dịch trong database
                Order order = orderService.findOrderById(transaction.getOrder().getId());

                // Nếu trạng thái trên payOS đã chuyên sang PAID và đơn hàng trong kho vẫn ở trạng thái Pending thì
                // thực hiện đưa thẻ cho khách hàng tương ứng với đơn hàng
                if(status.equals("PAID") && order.getStatus().equals("Pending")){
                    String action = orderService.createOrderDetail(order);

                    // xóa đơn hàng đó khỏi database queue
                    queueService.deleteQueueOrder(order.getId());

                    // lấy thông tin người chuyển
                    User user = userService.findById(transaction.getUser().getId());

                    // lưu lịch sử giao dịch
                    Transaction tran = ConvertJson.JsonToTransactionConverter.convertJsonToTransaction(paymentInfo, user, order);
                    // lưu transaction vào db
                    transactionService.saveTransaction(tran);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
