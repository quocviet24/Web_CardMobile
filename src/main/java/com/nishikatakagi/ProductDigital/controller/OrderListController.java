package com.nishikatakagi.ProductDigital.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.lib.payos.PayOS;
import com.lib.payos.type.ItemData;
import com.lib.payos.type.PaymentData;
import com.nishikatakagi.ProductDigital.controller.TransactionController.CheckoutController;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.CardType;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.OrderDetail;
import com.nishikatakagi.ProductDigital.model.Queue;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.CaptchaService;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.OrderService;
import com.nishikatakagi.ProductDigital.service.QueueService;
import com.nishikatakagi.ProductDigital.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/order")
@Controller
public class OrderListController {
    @Autowired
    private HttpSession session;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    CardTypeService cardTypeService;
    @Autowired
    QueueService queueService;
    private final PayOS payOS;

    public OrderListController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    Logger logger = LoggerFactory.getLogger(OrderListController.class);

    @GetMapping("")
    public String showOrderList(Model model, @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }

        User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));
        int pageSize = 7; // Số lượng đơn hàng trên mỗi trang
        Page<Order> orderPage = orderService.findAllOrderlistByUser(pageNo, pageSize, user);

        if (orderPage.isEmpty()) {
            model.addAttribute("error", "Không có đơn hàng nào");
        } else {
            List<Order> orders = orderPage.getContent();
            model.addAttribute("orders", orders);
        }

        if (session.getAttribute("errorRepurchase") != null) {
            model.addAttribute("error", session.getAttribute("errorRepurchase"));
            session.removeAttribute("errorRepurchase");
        }

        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/order?");
        return "publics/OrderList";
    }


    @GetMapping("/filter")
    public String showOrderListFilter(Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam String status) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));
        Integer pageSize = 7; // Số lượng đơn hàng trên mỗi trang
        Page<Order> orderPage = orderService.findAllOrderlistByUserFilter(pageNo, pageSize, user, status);
        if (orderPage.isEmpty()) {
            model.addAttribute("error", "Không có đơn hàng nào");
        } else {
            List<Order> orders = orderPage.getContent();
            model.addAttribute("orders", orders);
        }
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("status", status);
        model.addAttribute("action", "/order/filter?status=" + status + "&");
        return "publics/OrderList";
    }
    @GetMapping("/detail")
    public String showOrderDetail(Model model, @RequestParam int id) {
        Order order = orderService.findOrderById(id);
        if (order == null) {
            return "publics/404.html";
        }
        // check if the order belongs to the user
        User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));
        if (order.getUser().getId() != user.getId()) {
            // return 404
            return "publics/404.html";
        }
        List<OrderDetail> orderDetails = orderService.findOrderDetailByOrderId(id);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("order", order);
        if (!order.getStatus().equals("Completed")) {
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
        return "publics/OrderDetail";
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @RequestMapping(value = "/checkoutCancel")
    public String Cancel(RedirectAttributes model, @RequestParam(value = "orderid", required = false) Integer orderid, @RequestParam(value = "rd", required = false) Integer rd) {
        // Add your validation logic here
        if (orderid == null || rd == null || orderid <= 0 || rd <= 0) {
            return "redirect:/cart";
        }

        // Lấy map từ session
        CheckoutController.mapRandom = (HashMap<Integer, Integer>) session.getAttribute("successID");
        if (CheckoutController.mapRandom == null) {
            CheckoutController.mapRandom = new HashMap<>();
        }

        // Lấy giá trị từ map
        Integer storedValue = CheckoutController.mapRandom.get(orderid);

        // Kiểm tra giá trị trả về từ map
        if (storedValue == null || !storedValue.equals(rd)) {
            return "redirect:/";
        } else {
            if (CheckoutController.mapRandom != null) {
                Order order = orderService.findOrderById(orderid);
                orderService.updateOrderCancel(order, "Bạn đã hủy thanh toán đơn hàng này");
                queueService.deleteQueueOrder(orderid);
            }
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/repurchaseCheckoutCancel")
    public String RepurchaseCancel(RedirectAttributes model, @RequestParam(value = "orderid", required = false) Integer orderid, @RequestParam(value = "rd", required = false) Integer rd) {
        // Add your validation logic here
        if (orderid == null || rd == null || orderid <= 0 || rd <= 0) {
            return "redirect:/cart";
        }

        // Retrieve map from session
        CheckoutController.mapRandom = (HashMap<Integer, Integer>) session.getAttribute("successID");
        if (CheckoutController.mapRandom == null) {
            CheckoutController.mapRandom = new HashMap<>();
        }

        // Retrieve value from map
        Integer storedValue = CheckoutController.mapRandom.get(orderid);

        // Check returned value from map
        if (storedValue == null || !storedValue.equals(rd)) {
            return "redirect:/";
        } else {
            Order order = orderService.findOrderById(orderid);
            if (order != null) {
                orderService.updateOrderCancel(order, "Bạn đã hủy thanh toán đơn hàng này");
                queueService.deleteQueueOrder(orderid);
                return "redirect:/order";
            } else {
                // Handle case where no unique order is found
                // Log the issue or return an appropriate error message
                return "redirect:/order";
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/repurchase", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void rePurchase(RedirectAttributes model, @RequestParam(value = "id", required = false) Integer id,
                           HttpServletResponse httpServletResponse) {
        Order order = orderService.findOrderById(id);
        if (order == null) {
            returnOrder("Đơn hàng không tồn tại!!!", httpServletResponse);
            return;
        }
        // check if the order belongs to the user
        User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));
        if (order.getUser().getId() != user.getId()) {
            returnOrder("Đơn hàng không tồn tại!!!", httpServletResponse);
            return;
        }
        // check if the order is completed
        if (order.getStatus().equals("Pending")) {
            // xử lý check số lượng các mặt hàng của order này
            List<OrderDetail> orderDetails = orderService.findOrderDetailByOrderId(id);
            Map<CardType, Integer> cardTypeMap = new HashMap<>();
            for (OrderDetail orderDetail : orderDetails) {
                CardType cardType = orderDetail.getCardType();
                if (cardTypeMap.containsKey(cardType)) {
                    cardTypeMap.put(cardType, cardTypeMap.get(cardType) + 1);
                } else {
                    cardTypeMap.put(cardType, 1);
                }
            }
            boolean isEnough = true;
            for (Map.Entry<CardType, Integer> entry : cardTypeMap.entrySet()) {
                if (entry.getKey().getInStock() < entry.getValue()) {
                    isEnough = false;
                    break;
                }
            }
            if (!isEnough) {
                returnOrder("Số lượng thẻ của đơn hàng này hiện không đủ vui lòng thử lại sau!!!", httpServletResponse);
                return;
            }
            // xử lý thanh toán
            // Your existing code to generate the payment link and redirect to payOS
            try {
                int random = captchaService.createIDCaptcha();
                //check order id exist in map
                if(CheckoutController.mapRandom.containsKey(order.getId())){
                    //alert user cannot open two tabs
                    random = CheckoutController.mapRandom.get(order.getId());
                }else{
                    CheckoutController.mapRandom.put(order.getId(), random);
                    session.setAttribute("successID", CheckoutController.mapRandom);
                }
                CheckoutController.mapRandom.put(order.getId(), random);
                session.setAttribute("successID", CheckoutController.mapRandom);
                String description = "Thanh toan don hang";
                String returnUrl = "http://localhost:8080/success?orderid=" + order.getId() + "&rd=" + random;
                String cancelUrl = "http://localhost:8080/order/repurchaseCheckoutCancel?orderid=" + order.getId() + "&rd=" + random;
                String currentTimeString = String.valueOf(new Date().getTime());
                int orderCode = Integer.parseInt(currentTimeString.substring(currentTimeString.length() - 6));
                int total = 0;
                // Duyệt từng đơn hàng 1 và cho vào ItemList
                List<ItemData> itemList = new ArrayList<>();
                for (Map.Entry<CardType, Integer> entry : cardTypeMap.entrySet()) {
                    CardType cardType = entry.getKey();
                    int quantity = entry.getValue();
                    int price = (int) cardType.getUnitPrice();
                    ItemData item = new ItemData(cardType.getPublisher().getName(), quantity, price);
                    itemList.add(item);
                    total += quantity * price;
                }
                PaymentData paymentData = new PaymentData(orderCode, total, description,
                        itemList, cancelUrl, returnUrl);

                // Set the expiredAt field to 5 minute from now
                int fiveMinutesInSeconds = 300; // 5 minutes in seconds
                int currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
                int expirationTime = currentTimeInSeconds + fiveMinutesInSeconds;
                paymentData.setExpiredAt(expirationTime);

                JsonNode data = payOS.createPaymentLink(paymentData);
                if (data.has("orderCode")) {
                    queueService.deleteQueueOrder(id);
                    int paymentId = data.get("orderCode").asInt();
                    Queue queue = new Queue(paymentId, user, order);
                    queueService.saveQueueOrder(queue);
                } else {
                    System.out.println("Không tìm thấy trường 'orderCode' trong phản hồi từ API");
                }
                String checkoutUrl = data.get("checkoutUrl").asText();
                httpServletResponse.setHeader("Location", checkoutUrl);
                httpServletResponse.setStatus(302);

            } catch (Exception e) {
                System.out.println("Error during payment: " + e.getMessage());
                returnOrder("Lỗi trong quá trình thanh toán", httpServletResponse);
            }
        }
    }

    public void returnOrder(String error, HttpServletResponse httpServletResponse) {
        try {
            session.setAttribute("errorRepurchase", error);
            httpServletResponse.sendRedirect("/order");
        } catch (IOException | java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/checkout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void doCheckout(HttpServletRequest request, HttpServletResponse httpServletResponse,
                           RedirectAttributes model, HttpServletResponse response,
            @RequestParam(value = "cardType", required = false) Integer cardTypeId,
            @RequestParam(value = "quantity", required = false) String quantityy) throws java.io.IOException {
        Integer quantity ;
        try {
            quantity = Integer.parseInt(quantityy);
            if (quantity <= 0) {
                session.setAttribute("errorCheckoutHome", "Vui lòng nhập số dương");
                httpServletResponse.sendRedirect("/");
                return;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorCheckoutHome", "Vui lòng nhập số dương");
            httpServletResponse.sendRedirect("/");
            return;
        }
        User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));
        if (user == null) {
            session.setAttribute("errorCheckoutHome", "Vui lòng nhập một số dương");
            response.sendRedirect("/");
            return;
        }

        if (cardTypeId == null || quantity == null || quantity <= 0) {
            session.setAttribute("errorCheckoutHome", "Vui lòng kiểm tra lại thông tin đã chọn");
            response.sendRedirect("/");
            return;
        }
        // check card type have enough quantity
        CardType cardType = cardTypeService.findById(cardTypeId);
        if (cardType.getInStock() < quantity) {
            session.setAttribute("errorCheckoutHome", "Số lượng thẻ không đủ");
            response.sendRedirect("/");
            return;
        }

        // Create order

        Order order = orderService.saveOrder(user, cardType.getPublisher().getName(), quantity,
                cardType.getUnitPrice());
        if (order == null) {
            session.setAttribute("errorCheckoutHome", "Đã có lỗi xảy ra, vui lòng thử lại sau");
            response.sendRedirect("/");
            return;
        }
        for (int i = 0; i < quantity; i++) {
            orderService.saveOrderDetail(order, cardType);
        }

        try {
            // Your existing code to generate the payment link and redirect to payOS
            int random = captchaService.createIDCaptcha();
            // create id for each random
            // not done
            CheckoutController.mapRandom.put(order.getId(), random);
            session.setAttribute("successID", CheckoutController.mapRandom);
            String description = "Thanh toan don hang";
            String returnUrl = "http://localhost:8080/success?orderid=" + order.getId() + "&rd=" + random;
            String cancelUrl = "http://localhost:8080/order/checkoutCancel?orderid=" + order.getId() + "&rd=" + random ;
            String currentTimeString = String.valueOf(new Date().getTime());
            int orderCode = Integer.parseInt(currentTimeString.substring(currentTimeString.length() - 6));
            // Duyệt từng đơn hàng 1 và cho vào ItemList
            List<ItemData> itemList = new ArrayList<>();
            int total = 0;
            int price = (int) cardType.getUnitPrice();
            ItemData item = new ItemData(cardType.getPublisher().getName(), quantity, price);
            itemList.add(item);
            total += quantity * price;
            PaymentData paymentData = new PaymentData(orderCode, total, description,
                    itemList, cancelUrl, returnUrl);

            // Set the expiredAt field to 5 minute from now
            int fiveMinutesInSeconds = 300; // 5 minutes in seconds
            int currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
            int expirationTime = currentTimeInSeconds + fiveMinutesInSeconds;
            paymentData.setExpiredAt(expirationTime);

            JsonNode data = payOS.createPaymentLink(paymentData);
            if (data.has("orderCode")) {
                int paymentId = data.get("orderCode").asInt();
                Queue queue = new Queue(paymentId, user, order);
                queueService.saveQueueOrder(queue);
            } else {
                System.out.println("Không tìm thấy trường 'orderCode' trong phản hồi từ API");
            }
            String checkoutUrl = data.get("checkoutUrl").asText();
            httpServletResponse.setHeader("Location", checkoutUrl);
            httpServletResponse.setStatus(302);
        } catch (Exception e) {
            System.out.println("Error during payment: " + e.getMessage());
            returnHome("Lỗi trong quá trình thanh toán", model);
        }

    }

    public String returnHome(String error, RedirectAttributes model) {
        model.addAttribute("error", error);
        return "redirect:/";
    }
}
