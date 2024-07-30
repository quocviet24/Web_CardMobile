package com.nishikatakagi.ProductDigital.service_impl;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nishikatakagi.ProductDigital.dto.statistic.OrderByDayDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.TotalMoneyByMonthDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO;
import com.nishikatakagi.ProductDigital.model.*;
import com.nishikatakagi.ProductDigital.repository.*;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CardTypeRepository cardTypeRepository;
    @Autowired
    CardTypeService cardTypeService;
    @Autowired
    CartItemRepository cartItemRepository;
    private final Object lock = new Object();

    @Override
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public List<Card> findCardsByOrderId(int orderId) {
        // Fetch all OrderDetails for the given orderId
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return null;
    }

    @Override
    public List<Order> finfAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> findAllOrder(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> findOrderPending(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findByStatus("Pending", pageable);
    }

    @Override
    public Page<Order> findOrderCompleted(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findByStatus("Completed", pageable);
    }

    @Override
    public Page<Order> findOrderReject(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findByStatus("Reject", pageable);
    }

    @Override
    public Page<Order> findOrderByUser(Integer pageNo, Integer pageSize, User user) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public void rejectOrder(int id) {
        Order order = orderRepository.findById(id).get();
        order.setStatus("Reject");
        order.setNote("Đơn hàng đã bị hủy bởi quản trị viên");
        orderRepository.save(order);
    }

    @Override
    public boolean isPending(int id) {
        Order order = orderRepository.findById(id).get();
        return order.getStatus().equals("Pending");
    }

    @Override
    public Order findOrderById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    // checkout doi vs 1 hoac nhieu loai san pham
    @Override
    public String createOrderDetail(Order order) {
            if(!order.getStatus().equals("Pending")) {
                return "Đơn hàng không được duyệt để thực hiện!";
            }
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
            // check for quantity of card
            Map<CardType,Integer> cardTypeQuantity = new HashMap<>();
            for(OrderDetail orderDetail : orderDetails) {
                if(orderDetail.getCardType().getIsDeleted()==true){
                    order.setStatus("Error");
                    order.setNote("Đơn hàng này chứa mặt hàng ngừng hoạt động, vui lòng liên hệ với admin để được hỗ trợ");
                    orderRepository.save(order);
                    return "Xin lỗi quý khách hiện đơn hàng này chứa mặt hàng ngừng hoạt động";
                }
                if (cardTypeQuantity.get(orderDetail.getCardType()) == null) {
                    cardTypeQuantity.put(orderDetail.getCardType(), 1);
                } else {
                    cardTypeQuantity.put(orderDetail.getCardType(), (int) cardTypeQuantity.get(orderDetail.getCardType().getId()) + 1);
                }
            }
            for (Map.Entry<CardType, Integer> entry : cardTypeQuantity.entrySet()) {
                CardType cardType = entry.getKey();
                Integer quantity = entry.getValue();
                if (cardType.getInStock() < quantity) {
                    order.setStatus("Error");
                    order.setNote("Đơn hàng này không đủ số lượng trong kho, vui lòng liên hệ với admin để được hỗ trợ");
                    orderRepository.save(order);
                    return "Xin lỗi quý khách " +"hiện đang không đủ số lượng trong kho";
                }
            }
            for (OrderDetail orderDetail : orderDetails) {
                List<Card> listCardByCardType = cardRepository
                        .findByCardTypeAndIsDeletedOrderByExpiryDateAsc(orderDetail.getCardType(), false);
                for (Card card : listCardByCardType) {
                    if (!card.getExpiryDate().before(java.util.Date.from(Instant.now()))) {
                        card.setIsDeleted(true);
                        card.setDeletedBy(order.getUser().getId());
                        card.setDeletedDate(new Date(System.currentTimeMillis()));
                        cardRepository.save(card);
                        orderDetail.setCardId(card.getId());
                        orderDetail.setCardNumber(card.getCardNumber());
                        orderDetail.setExpiryDate(card.getExpiryDate());
                        orderDetail.setSeriNumber(card.getSeriNumber());
                        orderDetailRepository.save(orderDetail);
                        cardTypeRepository.findById(card.getCardType().getId()).ifPresent(cardType -> {
                            cardType.setInStock(cardType.getInStock() - 1);
                            cardType.setSoldQuantity(cardType.getSoldQuantity() + 1);
                            if (cardType.getInStock() == 0) {
                                // delete auto by system
                                cardType.setIsDeleted(true);
                                cardType.setDeletedBy(0);
                                cardType.setDeletedDate(new Date(System.currentTimeMillis()));
                            }
                            cardTypeRepository.save(cardType);
                        });
                        break;
                    } else {
                        // delete card cannot used
                        card.setIsDeleted(true);
                        card.setDeletedBy(0);
                        card.setDeletedDate(new Date(System.currentTimeMillis()));
                        cardRepository.save(card);
                    }
                }
            }
            order.setStatus("Completed");
            order.setNote("Đơn hàng thanh toán thành công");
            orderRepository.save(order);
            return "Đơn hàng đã hoàn thành!";
    }

    @Override
    public Order saveOrders(User user, List<String> publisherName, List<Integer> quantity, List<String> unitPrice) {
        Order order = new Order();
        order.setUser(user);
        java.util.Date date = new java.util.Date();
        order.setOrderDate(date);

        double totalMoney = 0;
        for (int i = 0; i < publisherName.size(); i++) {
            int price = Integer.parseInt(unitPrice.get(i));
            totalMoney += quantity.get(i) * price;
        }

        order.setTotalMoney(totalMoney);
        order.setStatus("Pending");
        orderRepository.save(order);

        return order;
    }

    @Override
    public void updateOrderCancel(Order order, String mess) {
        order.setStatus("Cancel");
        order.setNote(mess);
        orderRepository.save(order);
    }

    @Override
    public Page<Order> findAllOrderlistByUser(Integer pageNo, Integer pageSize, User user) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderRepository.findByUser(user, pageable);
    }


    @Override
    public Page<Order> findOrderError(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findByStatus("Error", pageable);
    }

    @Override
    public List<UserStatDTO> getTopUsersWithLargestTotalMoney() {
        Pageable pageable = PageRequest.of(0, 10);
        return orderRepository.findUserWithLargestTotalMoney(pageable);
    }

    @Override
    public List<UserStatDTO> getTopUsersWithLargestTotalMoney(java.util.Date startDate, java.util.Date endDate) {
        Pageable pageable = PageRequest.of(0, 10);
        return orderRepository.findUserWithLargestTotalMoney(pageable, convertToLocalDateTime(startDate),
                convertToLocalDateTime(endDate));
    }

    @Override
    public List<OrderByDayDTO> getOrdersByDayInCurrentWeek() {
        List<Object[]> results = orderRepository.getOrderCountsByDay();
        List<OrderByDayDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            OrderByDayDTO dto = new OrderByDayDTO();
            dto.setNumberOfOrder(((Number) result[0]).intValue()); // Assuming numberOfOrder is an integer
            dto.setDay((Date) result[1]); // Assuming day is a java.util.Date
            dtos.add(dto);
        }

        return dtos;
    }

    public java.time.LocalDateTime convertToLocalDateTime(java.util.Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public List<TotalMoneyByMonthDTO> getTotalMoneyByMonthAndYear(int year) {
        List<Object[]> results = orderRepository.getTotalMoneyByMonthAndYear(year);
        List<TotalMoneyByMonthDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            int month = (int) result[0];
            double totalMoney = (double) result[1];
            dtos.add(new TotalMoneyByMonthDTO(month, totalMoney));
        }
        return dtos;
    }

    @Override
    public List<OrderDetail> findOrderDetailByOrderId(int id) {
        return orderDetailRepository.findByOrderId(id);
    }

    @Override
    public void saveOrderDetail(Order order, CardType cardType) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setCardType(cardType);
        orderDetail.setPublisherName(cardType.getPublisher().getName());
        orderDetail.setUnitPrice(cardType.getUnitPrice());
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public Order saveOrder(User user, String publisherName, Integer quantity, double unitPrice) {
        Order order = new Order();
        order.setUser(user);
        java.util.Date date = new java.util.Date();
        order.setOrderDate(date);
        double totalMoney = quantity * unitPrice;
        order.setTotalMoney(totalMoney);
        order.setStatus("Pending");
        orderRepository.save(order);

        return order;
    }

    @Override
    public Page<Order> findAllOrderlistByUserFilter(Integer pageNo, Integer pageSize, User user, String status) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        if(status.equals("all")) {
            return orderRepository.findByUser(user, pageable);
        }
        return orderRepository.findByUserAndStatus(user, status, pageable);
    }
}
