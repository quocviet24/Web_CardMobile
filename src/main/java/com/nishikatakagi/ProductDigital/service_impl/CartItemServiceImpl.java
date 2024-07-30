package com.nishikatakagi.ProductDigital.service_impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.CartItem;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.CartItemRepository;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.CartItemService;
import com.nishikatakagi.ProductDigital.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    UserService userService;
    @Autowired
    CardTypeService cardTypeService;

    @Override
    public List<CartItem> getCartItemsForUser(int userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public String addItemToCart(String username, Integer cardTypeId, Integer quantity) {
        User user = userService.findByUsername(username);
        CardType cardType = cardTypeService.findById(cardTypeId);
        if(cardType == null){
            return "Không tìm thấy sản phẩm phù hợp!";
        }
        if(cardType.getIsDeleted() || cardType.getPublisher().getIsDeleted()){
            return "Sản phẩm đã bị xóa!";
        }
        if (cardTypeId == null || quantity == null) {
            return "Vui lòng chọn sản phẩm và số lượng!";
        }
        if (quantity <= 0 || quantity > cardType.getInStock()) {
            return "Số lượng hiện tại không đủ trong kho!";
        }
        // Check if item already exists in cart and update quantity if needed
        CartItem cartItem = cartItemRepository.findByUserIdAndCardTypeId(user.getId(), cardTypeId);
        if (cartItem != null) {
            if (quantity <= 0 || quantity + cartItem.getQuantity() > cardType.getInStock()) {
                return "Thêm vào giỏ hàng thất bại, sản phẩm đã tồn tại trong giỏ hàng, số lượng bạn nhập thêm kho không đủ để cung cấp!";
            }
            // Update existing item with the same cardTypeId
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotal(cartItem.getTotal() + cardType.getUnitPrice() * quantity);
            cartItemRepository.save(cartItem);
            return "Thêm vào giỏ hàng thành công!";
        } else {
            if (quantity <= 0 || quantity > cardType.getInStock()) {
                return "Số lượng không hợp lệ!";
            }
            // Create new CartItem
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setCardType(cardType);
            cartItem.setQuantity(quantity);
            cartItem.setTotal(cardType.getUnitPrice() * quantity);
            cartItemRepository.save(cartItem);
            return "Thêm vào giỏ hàng thành công!";
        }
    }

    @Override
    public CartItem updateItemQuantity(String username, int cardTypeId, int newQuantity) {
        User user = userService.findByUsername(username);

        if (user != null) {
            CartItem cartItem = cartItemRepository.findByUserIdAndCardTypeId(user.getId(), cardTypeId);

            if (cartItem != null) {
                CardType cardType = cardTypeService.findById(cardTypeId);
                if (cardType == null) {
                    return null;
                }

                int stockQuantity = cardType.getInStock();
                int adjustedQuantity = Math.min(newQuantity, stockQuantity);

                if (stockQuantity == 0) {
                    adjustedQuantity = 0;
                }

                cartItem.setQuantity(adjustedQuantity);
                cartItem.setTotal(cartItem.getCardType().getUnitPrice() * adjustedQuantity);

                return cartItemRepository.save(cartItem);
            }
        }
        return null;
    }

    @Override
    public List<String> adjustCartItemQuantities(String username) {
        List<String> adjustments = new ArrayList<>();
        User user = userService.findByUsername(username);
        if (user != null) {
            List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
            for (CartItem item : cartItems) {
                CardType cardType = item.getCardType();
                if (cardType.getInStock() == 0) {
                    // If stock is zero, set cart item quantity to zero
                    item.setQuantity(0);
                    item.setTotal(0.0);
                    adjustments.add(cardType.getPublisher().getName() + " " + cardType.getUnitPrice()
                            + ": số lượng đặt về 0 do hết hàng.");
                } else if (cardType.getInStock() < item.getQuantity()) {
                    // If stock is less than cart quantity, adjust to match stock
                    adjustments.add(cardType.getPublisher().getName() + " " + cardType.getUnitPrice()
                            + ": số lượng thay đổi từ " + item.getQuantity()
                            + " về " + cardType.getInStock() + " do thay đổi số lượng trong kho.");
                    item.setQuantity(cardType.getInStock());
                    item.setTotal(cardType.getUnitPrice() * cardType.getInStock());
                }
                cartItemRepository.save(item);
            }
        }
        return adjustments;
    }

    @Override
    public double calculateCartTotal(int userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
    }

    @Override
    public List<CartItem> getCartDetails(String username) {
        User user = userService.findByUsername(username);
        return cartItemRepository.findByUserId(user.getId());
    }

    @Override
    public void deleteCartItem(int cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteCartItemByCardType(int cardTypeId) {
        cartItemRepository.deleteByCardTypeId(cardTypeId);
    }

    @Override
    public void updateQuantity(int cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            cartItem.setTotal(cartItem.getCardType().getUnitPrice() * quantity); // Update total
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public int getCartItemCount(String username) {
        List<CartItem> cartItems = cartItemRepository.findByUserUsername(username);
        return cartItems.size();
    }

}
