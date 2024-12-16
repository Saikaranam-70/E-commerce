package com.SaGa.Project.service;

import com.SaGa.Project.model.Cart;
import com.SaGa.Project.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CartService {
    Cart addItemToCart(String userId, CartItem cartItem);

    Optional<Cart> getCartByUserId(String userId);

    Cart removeItemFromCart(String userId,CartItem cartItem);

    Cart clearCart(String userId);
}
