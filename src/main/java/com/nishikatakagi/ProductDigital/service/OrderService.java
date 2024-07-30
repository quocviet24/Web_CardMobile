package com.nishikatakagi.ProductDigital.service;

import java.util.Date;
import java.util.List;

import com.nishikatakagi.ProductDigital.dto.statistic.OrderByDayDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.TotalMoneyByMonthDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO;
import com.nishikatakagi.ProductDigital.model.Card;
import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.OrderDetail;
import com.nishikatakagi.ProductDigital.model.User;
import org.springframework.data.domain.Page;

public interface OrderService {
    List<Order> findOrdersByUser(User user);

    List<Card> findCardsByOrderId(int orderId);

    List<Order> finfAllOrder();

    Page<Order> findAllOrder(Integer pageNo, Integer pageSize);

    Page<Order> findOrderPending(Integer pageNo, Integer pageSize);

    Page<Order> findOrderCompleted(Integer pageNo, Integer pageSize);

    Page<Order> findOrderReject(Integer pageNo, Integer pageSize);

    Page<Order> findOrderError(Integer pageNo, Integer pageSize);

    Page<Order> findOrderByUser(Integer pageNo, Integer pageSize, User username);

    void rejectOrder(int id);

    boolean isPending(int id);

    //ngoc
    Order findOrderById(int id);

    String createOrderDetail(Order order);

    //viet
    Order saveOrders(User user, List<String> publisherName, List<Integer> quantity, List<String> unitPrice);

    void updateOrderCancel(Order order, String mess);
    //huy
    Page<Order> findAllOrderlistByUser(Integer pageNo, Integer pageSize, User user);

    List<UserStatDTO> getTopUsersWithLargestTotalMoney();

    List<UserStatDTO> getTopUsersWithLargestTotalMoney(Date startDate, Date endDate);

    List<OrderByDayDTO> getOrdersByDayInCurrentWeek();

    List<TotalMoneyByMonthDTO> getTotalMoneyByMonthAndYear(int year);

    List<OrderDetail> findOrderDetailByOrderId(int id);
    //ngoc
    void saveOrderDetail(Order order, CardType cardType);
    Order saveOrder(User user, String publisherName, Integer quantity, double unitPrice);

    Page<Order> findAllOrderlistByUserFilter(Integer pageNo, Integer pageSize, User user, String status);
}
