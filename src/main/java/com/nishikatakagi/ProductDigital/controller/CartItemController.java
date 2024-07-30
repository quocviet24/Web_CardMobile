package com.nishikatakagi.ProductDigital.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.CartItem;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.CartItemService;
import com.nishikatakagi.ProductDigital.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/cart")
@Controller
public class CartItemController {

    @Autowired
    HttpSession session;
    @Autowired
    CardTypeService cardTypeService;
    @Autowired
    CartItemService cartItemService;
    @Autowired
    UserService userService;

    @GetMapping("/add")
    public Object addToCart(
            HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "cardTypeId", required = false) Integer cardTypeId,
            @RequestParam(value = "quantity", required = false) Integer quantity) {
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            if (isAjaxRequest(request)) {
                return new ResponseEntity<>("ERROR_NOT_LOGGED_IN", HttpStatus.UNAUTHORIZED);
            } else {
                return new ModelAndView("redirect:/login");
            }
        }

        String message = cartItemService.addItemToCart(userSession.getUsername(), cardTypeId, quantity);

        if (isAjaxRequest(request)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonResponse = objectMapper.writeValueAsString(Map.of("message", message));
                return ResponseEntity.ok().body(jsonResponse);
            } catch (Exception e) {
                return new ResponseEntity<>("ERROR_INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            redirectAttributes.addFlashAttribute("message", message);
            return new ModelAndView("redirect:/");
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @GetMapping("")
    public String showCartDetails(Model model) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            List<String> adjustments = cartItemService.adjustCartItemQuantities(user.getUsername());
            if (!adjustments.isEmpty()) {
                model.addAttribute("adjustments", adjustments);
            }
            List<CartItem> cart = cartItemService.getCartDetails(user.getUsername());
            model.addAttribute("cart", cart);
            return "publics/cart";
        } else {
            return "redirect:/login";
        }
    }

    @DeleteMapping("/delete")
    public String deleteCartItem(@RequestParam("cartItemId") int cartItemId, RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            cartItemService.deleteCartItem(cartItemId);
            return "redirect:/cart";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/updateQuantity")
    public ResponseEntity<String> updateQuantity(@RequestParam int quantity, @RequestParam int cardTypeId) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            try {
                // Update the quantity in the database using username
                CartItem updatedCartItem = cartItemService.updateItemQuantity(user.getUsername(), cardTypeId, quantity);

                if (updatedCartItem != null) {
                    return ResponseEntity.ok("Quantity updated successfully");
                } else {
                    return ResponseEntity.badRequest().body("Failed to update quantity");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during update");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }

    @GetMapping("/itemsCount")
    public ResponseEntity<?> getCartItemCount() {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");

        // Create a map to return both loggedIn status and item count
        Map<String, Object> response = new HashMap<>();
        response.put("loggedIn", user != null);

        // if user logs in, return the number of items in cart.
        if (user != null) {
            try {
                response.put("cartItemCount", cartItemService.getCartItemCount(user.getUsername()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // if user has not logged in yet, return 0
            response.put("cartItemCount", 0);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove/{itemId}")
    public ResponseEntity<Map<String, Boolean>> removeFromCart(@PathVariable int itemId) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            try {
                cartItemService.deleteCartItem(itemId);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            } catch (Exception e) {
                return ResponseEntity.ok(Collections.singletonMap("success", false));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("success", false));
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems() {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            List<CartItem> cartItems = cartItemService.getCartDetails(user.getUsername());
            return ResponseEntity.ok(cartItems);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
