package com.nishikatakagi.ProductDigital.controller.TransactionController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lib.payos.PayOS;
import com.lib.payos.type.ItemData;
import com.lib.payos.type.PaymentData;
import com.nishikatakagi.ProductDigital.common.ConvertJson;
import com.nishikatakagi.ProductDigital.common.OrderQueue;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.*;
import com.nishikatakagi.ProductDigital.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {
    private final PayOS payOS;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    QueueService queueService;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    CaptchaService captchaService;

    @Autowired
    CardTypeService cardTypeService;

    @Autowired
    UserService userService;

    @Autowired
    HttpSession session;

    @Autowired
    OrderQueue orderQueue;

    public static Map<Integer, Integer> mapRandom = new HashMap<>();

    public CheckoutController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @GetMapping("/test")
    @ResponseBody
    public String createOrder() {
        Order order = orderService.findOrderById(12);
        orderQueue.push(order);
        return "redirect:/";
    }

    @RequestMapping(value = "/success")
    public String Success(RedirectAttributes model, @RequestParam(value = "orderid", required = false) Integer orderid,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "rd", required = false) Integer rd) {
        if (url == null) {
            url = "";
        }
        // Add your validation logic here
        if (orderid == null || rd == null || orderid <= 0 || rd <= 0) {
            return "redirect:/"+url;
        }
        mapRandom = (HashMap<Integer, Integer>) session.getAttribute("successID");
        if (mapRandom == null) {
            mapRandom = new HashMap<>();
        }
        
        if (mapRandom != null && !mapRandom.get(orderid).equals(rd)) {
            return "redirect:/" + url;
        } else {
            if (mapRandom != null) {
                Order order = orderService.findOrderById(orderid);
                orderQueue.push(order);

                // Xóa sản phẩm mua trong giỏ hàng
                if(url.equals("cart")){
                    List<OrderDetail> list = orderDetailService.findListOrderDetailByOrderId(orderid);
                    for (OrderDetail orderDetail : list){
                        cartItemService.deleteCartItemByCardType(orderDetail.getCardType().getId());
                    }
                }

                // Tìm và lưu trữ transaction vào bảng transaction
                // đồng thời xóa đơn giao dịch ở queue đi
                // Tìm bảng row trong queue ( database ) bằng orderId
                try {
                    Queue queue = queueService.findQueueByOrderId(orderid);
                    JsonNode paymentInfo = payOS.getPaymentLinkInfomation(queue.getOrderTransaction());
                    User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));

                    Transaction tran = ConvertJson.JsonToTransactionConverter.convertJsonToTransaction(paymentInfo, user, order);
                    // lưu transaction vào db
                    transactionService.saveTransaction(tran);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mapRandom.remove(orderid);
                return "redirect:/order/detail?id=" + orderid;
            }
        }
        return "redirect:/" + url;
    }

    @RequestMapping(value = "/cartCancel")
    public String Cancel(RedirectAttributes model, @RequestParam(value = "orderid", required = false) Integer orderid, @RequestParam(value = "rd", required = false) Integer rd) {
        // Add your validation logic here
        if (orderid == null || rd == null || orderid <= 0 || rd <= 0) {
            return "redirect:/cart";
        }
        mapRandom = (HashMap<Integer, Integer>) session.getAttribute("successID");
        if (mapRandom == null) {
            mapRandom = new HashMap<>();
        }
        if (mapRandom != null && !mapRandom.get(orderid).equals(rd)) {
            return "redirect:/cart";
        } else {
            if (mapRandom != null) {
                Order order = orderService.findOrderById(orderid);
                orderService.updateOrderCancel(order, "Bạn đã hủy thanh toán đơn hàng này");
                queueService.deleteQueueOrder(orderid);
            }
        }
        return "redirect:/cart";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create-payment-link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void checkout(RedirectAttributes model, HttpServletResponse httpServletResponse,
            @RequestParam List<String> publisherName,
            @RequestParam List<Integer> quantity, @RequestParam List<String> unitPrice) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
        if (userSessionDto == null)
            returnLogin();
        try {
            int sizeCart = sizeCart(publisherName, quantity, unitPrice);
            if (sizeCart < 0) {
                returnCart();
            } else {
                // Kiểm tra số lượng
                String error = "";
                for (int i = 0; i < publisherName.size(); i++) {
                    CardType cardType = cardTypeService.findCarfTypeByPublisherNameAndUnitPrice(publisherName.get(i),
                            Integer.parseInt(unitPrice.get(i)));
                    if (cardType.getInStock() < quantity.get(i)) {
                        error += "" + publisherName.get(i) + " mệnh giá " + unitPrice.get(i);
                    }
                }
                if (!error.isEmpty()) {
                    // dung model binh thuong
                    returnCartAndError("Xin lỗi quý khách " + error + " hiện đang không đủ số lượng trong kho", model);
                    return;
                } else {
                    // Lưu thông tin đơn hàng vào orders
                    User user = userService.findUserDBByUserSession(userSessionDto);
                    Order order = orderService.saveOrders(user, publisherName, quantity, unitPrice);

                    // Lưu thông tin đơn hàng vào order detail
                    for (int i = 0; i < publisherName.size(); i++) {
                        CardType cardType = cardTypeService.findCarfTypeByPublisherNameAndUnitPrice(
                                publisherName.get(i), Integer.parseInt(unitPrice.get(i)));
                        for (int j = 0; j < quantity.get(i); j++) {
                            orderService.saveOrderDetail(order, cardType);
                        }
                    }
                    // Tạo id cho việc thanh toán thành công
                    int random = captchaService.createIDCaptcha();
                    mapRandom.put(order.getId(), random);
                    session.setAttribute("successID", mapRandom);
                    String description = "Thanh toan don hang";
                    String returnUrl = apiBaseUrl + "/success?orderid=" + order.getId() + "&rd=" + random + "&url=cart";
                    String cancelUrl = apiBaseUrl + "/cartCancel?orderid=" + order.getId() + "&rd=" + random ;
                    // Gen order code
                    String currentTimeString = String.valueOf(new Date().getTime());
                    int orderCode = Integer.parseInt(currentTimeString.substring(currentTimeString.length() - 6));
                    // Duyệt từng đơn hàng 1 và cho vào ItemList
                    List<ItemData> itemList = new ArrayList<>();
                    int total = 0;
                    for (int i = 0; i < sizeCart; i++) {
                        int price = Integer.parseInt(unitPrice.get(i));
                        ItemData item = new ItemData(publisherName.get(i), quantity.get(i), price);
                        itemList.add(item);
                        total += quantity.get(i) * price;
                    }
                    PaymentData paymentData = new PaymentData(orderCode, total, description,
                            itemList, cancelUrl, returnUrl);

                    // Set the expiredAt field to 5 minute from now
                    int fiveMinutesInSeconds = 300; // 5 minutes in seconds
                    int currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
                    int expirationTime = currentTimeInSeconds + fiveMinutesInSeconds;
                    paymentData.setExpiredAt(expirationTime);

                    JsonNode data = payOS.createPaymentLink(paymentData);
                    // Lưu order_id vào database
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
                }
            }
        } catch (Exception e) {
            System.out.println("Có lỗi khi thanh toán");
        }
    }

    @GetMapping(value = "/payment-info/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonNode> getPaymentInfo(@PathVariable int orderCode, HttpSession session) {
        try {
            JsonNode paymentInfo = payOS.getPaymentLinkInfomation(orderCode);
            User user = userService.findUserDBByUserSession((UserSessionDto) session.getAttribute("user_sess"));

            //Transaction tran = ConvertJson.JsonToTransactionConverter.convertJsonToTransaction(paymentInfo, user, );
            //transactionService.saveTransaction(tran);
            //System.out.println(tran.toString());

            return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private int sizeCart(List<String> publisherName, List<Integer> quantity, List<String> unitPrice) {
        int sizepublisherName = publisherName.size();
        int sizeQuantity = quantity.size();
        int sizeUnitPrice = unitPrice.size();

        // xác nhận số lượng ở mỗi thuộc tính là bằng nhau
        if (sizepublisherName == sizeQuantity && sizepublisherName == sizeUnitPrice) {
            // trả về số lượng giỏ hàng
            return sizeUnitPrice;
        } else {
            // nếu số lượng k đồng nhất thì quay ngược lại về cart
            return -1;
        }
    }

    public String returnCart() {
        return "redirect:/cart";
    }

    public String testSuccessTransaction(String url) {
        return url;
    }

    public String returnCartAndError(String error, RedirectAttributes model) {
        model.addFlashAttribute("errorNotStock", error);
        return "redirect:/cart";
    }

    public String returnLogin() {
        return "redirect:/";
    }
}