package com.SaGa.Project.service;

import com.SaGa.Project.model.CartItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface CartItemService {
    CartItem addCartItem(CartItem cartItem);

    Optional<CartItem> getCartItemById(String id);

    List<CartItem> getAllCartItems();

    void deleteCartItemById(String id);

    CartItem updateCartItem(String id, int quantity, BigDecimal price);
    List<CartItem> getCartItemByProductId(String id);
}
