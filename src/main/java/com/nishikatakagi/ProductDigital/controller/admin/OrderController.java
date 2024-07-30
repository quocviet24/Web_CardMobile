package com.nishikatakagi.ProductDigital.controller.admin;

import com.nishikatakagi.ProductDigital.model.*;
import com.nishikatakagi.ProductDigital.service.OrderService;
import com.nishikatakagi.ProductDigital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/orderAdmin")
@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @GetMapping("")
    public String showPage(Model model, @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        Page<Order> listOrder = orderService.findAllOrder(pageNo, 7);
        if (listOrder.isEmpty()) {
            model.addAttribute("error", "Không có tài khoản nào");
        } else {
            List<Order> listOrder1 = listOrder.getContent();
            model.addAttribute("listOrder", listOrder1);
        }
        model.addAttribute("status", "default");
        model.addAttribute("totalPages", listOrder.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/orderAdmin?");
        return "pages/order/orderAdmin.html";
    }

    @GetMapping("/detail")
    public String showOrderDetailAdmin(Model model, @RequestParam int id) {
        Order order = orderService.findOrderById(id);
        List<OrderDetail> orderDetails = orderService.findOrderDetailByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        if(!order.getStatus().equals("Completed")) {
            Map<CardType, Integer> cardTypeMap = new HashMap<>();
            for (OrderDetail orderDetail : orderDetails) {
                CardType cardType = orderDetail.getCardType();
                if (cardTypeMap.containsKey(cardType)) {
                    cardTypeMap.put(cardType, cardTypeMap.get(cardType) + 1);
                } else {
                    cardTypeMap.put(cardType, 1);
                }
            }
            model.addAttribute("cardTypeMap", cardTypeMap);
        }
        return "pages/order/orderDetail";

    }

    @GetMapping("/reject")
    public String rejectOrrder(Model model, @RequestParam int id) {
        // Kiểm tra, nếu đúng order này trong database có trạng thái Pending thì tiến
        // hành reject
        if (orderService.isPending(id)) {
            orderService.rejectOrder(id);
            return "redirect:/orderAdmin";
        } else {
            // Nếu không đúng thì thôi, không làm gì
            return "redirect:/orderAdmin";
        }
    }

    @GetMapping("/filter")
    public String ShowOrderFilter(Model model, @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        if (status == null) {
            status = "default"; // set a default status
        }
        switch (status) {
            case "Completed":
                System.out.println("Completed");
                Page<Order> listOrderCompleted = orderService.findOrderCompleted(pageNo, 7);
                if (listOrderCompleted.isEmpty()) {
                    model.addAttribute("error", "Không có tài khoản nào");
                } else {
                    List<Order> listOrderCompleted1 = listOrderCompleted.getContent();
                    model.addAttribute("listOrder", listOrderCompleted1);
                }
                model.addAttribute("totalPages", listOrderCompleted.getTotalPages());
                model.addAttribute("pageNo", pageNo);
                model.addAttribute("status", "Completed");
                model.addAttribute("action", "/orderAdmin/filter?status=Completed&");
                return "pages/order/orderAdmin.html";
            case "Pending":
                System.out.println("Pending");
                Page<Order> listOrderPending = orderService.findOrderPending(pageNo, 7);
                if (listOrderPending.isEmpty()) {
                    model.addAttribute("error", "Không có tài khoản nào");
                } else {
                    List<Order> listOrderPending1 = listOrderPending.getContent();
                    model.addAttribute("listOrder", listOrderPending);
                }
                model.addAttribute("totalPages", listOrderPending.getTotalPages());
                model.addAttribute("pageNo", pageNo);
                model.addAttribute("status", "Pending");
                model.addAttribute("action", "/orderAdmin/filter?status=Pending&");
                return "pages/order/orderAdmin.html";
            case "Reject":
                System.out.println("Reject");
                Page<Order> listOrderReject = orderService.findOrderReject(pageNo, 7);
                if (listOrderReject.isEmpty()) {
                    model.addAttribute("error", "Không có tài khoản nào");
                } else {
                    List<Order> listOrderReject1 = listOrderReject.getContent();
                    model.addAttribute("listOrder", listOrderReject1);
                }
                model.addAttribute("totalPages", listOrderReject.getTotalPages());
                model.addAttribute("pageNo", pageNo);
                model.addAttribute("status", "Reject");
                model.addAttribute("action", "/orderAdmin/filter?status=Reject&");
                return "pages/order/orderAdmin.html";
            case "Error":
                System.out.println("Error");
                Page<Order> listOrderError = orderService.findOrderError(pageNo, 7);
                if (listOrderError.isEmpty()) {
                    model.addAttribute("error", "Không có tài khoản nào");
                } else {
                    List<Order> listOrderError1 = listOrderError.getContent();
                    model.addAttribute("listOrder", listOrderError1);
                }
                model.addAttribute("totalPages", listOrderError.getTotalPages());
                model.addAttribute("pageNo", pageNo);
                model.addAttribute("status", "Error");
                model.addAttribute("action", "/orderAdmin/filter?status=Error&");
                return "pages/order/orderAdmin.html";
            case "default":
                System.out.println("quay về trang đầu");
                return "redirect:/orderAdmin";
            default:
                return "redirect:/orderAdmin";
        }
    }

    @GetMapping("/filterByAccount")
    public String orderFilterByAccount(Model model, @RequestParam(value = "username", required = false) String username,
            @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        if (!username.isEmpty()) {
            User user = userService.findByUsername(username);
            Page<Order> listOrder = orderService.findOrderByUser(pageNo, 7, user);
            if (listOrder.isEmpty()) {
                model.addAttribute("error", "Không có tài khoản nào");
            } else {
                List<Order> listOrder1 = listOrder.getContent();
                model.addAttribute("listOrder", listOrder1);
            }
            model.addAttribute("status", "default");
            model.addAttribute("totalPages", listOrder.getTotalPages());
            model.addAttribute("pageNo", pageNo);
            String action = "filterByAccount?username=" + username + "&";
            model.addAttribute("action", action);
            return "pages/order/orderAdmin.html";
        } else {
            return "redirect:/orderAdmin";
        }
    }
}
