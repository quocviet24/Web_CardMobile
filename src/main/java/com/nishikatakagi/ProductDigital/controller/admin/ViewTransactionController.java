package com.nishikatakagi.ProductDigital.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.OrderDetail;
import com.nishikatakagi.ProductDigital.model.Transaction;
import com.nishikatakagi.ProductDigital.repository.OrderDetailRepository;
import com.nishikatakagi.ProductDigital.service.TransactionService;

@Controller
@RequestMapping("/transaction")
public class ViewTransactionController {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("")
    public String showPageTransaction(Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String searchColumn,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String bankName) {
                
        int pageSize = 7;
        Page<Transaction> transactionPage = transactionService.getTransactions(pageNo, pageSize, status, dateFrom,
                dateTo, searchColumn, searchTerm);

        if (transactionPage.isEmpty()) {
            model.addAttribute("error", "Không có giao dịch nào");
            pageNo = 1; // Reset to page 1 if no results
        } else {
            model.addAttribute("transactions", transactionPage.getContent());
        }

        model.addAttribute("bankName", bankName);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("status", status);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("searchColumn", searchColumn);
        model.addAttribute("searchTerm", searchTerm);

        return "pages/transaction/transaction.html";
    }

    @GetMapping("/order-details/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable int orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

            if (orderDetails.isEmpty()) {
                response.put("success", false);
                response.put("message", "Order details not found for this order.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Order order = orderDetails.get(0).getOrder(); // Get order from the first detail
            response.put("success", true);
            response.put("order", order);
            response.put("orderDetails", orderDetails);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
