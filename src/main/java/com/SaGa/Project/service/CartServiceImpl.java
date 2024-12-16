package com.SaGa.Project.service;

import com.SaGa.Project.exception.CartNotFoundException;
import com.SaGa.Project.exception.UserNotFoundException;
import com.SaGa.Project.model.Cart;
import com.SaGa.Project.model.CartItem;
import com.SaGa.Project.model.User;
import com.SaGa.Project.repository.CartRepository;
import com.SaGa.Project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart createCart(String userId){
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalPrice(BigDecimal.ZERO);
        return cartRepository.save(cart);
    }

    @Override
    public Cart addItemToCart(String userId, CartItem cartItem) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Cart cart = cartOptional.orElseGet(() -> createCart(userId));
            cart.addItem(cartItem);
            User user = optionalUser.get();
            user.setCart(cart);
            userRepository.save(user);
            return cartRepository.save(cart);
        }else {
            throw new UserNotFoundException("User not found with the Id :"+ userId);
        }

    }

    @Override
    public Optional<Cart> getCartByUserId(String userId) {
        return Optional.ofNullable(cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user with ID: " + userId)));
    }

    @Override
    public Cart removeItemFromCart(String userId, CartItem cartItem) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if(optionalCart.isPresent()){
            Cart cart = optionalCart.get();
            cart.removeItem(cartItem);
            return cartRepository.save(cart);
        }else {
            throw new CartNotFoundException("Cart not found");
        }

    }

    @Override
    public Cart clearCart(String userId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if(optionalCart.isPresent()){
            Cart cart = optionalCart.get();
            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cart = cartRepository.save(cart);

            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalCart.isPresent()){
                User user = optionalUser.get();
                user.setCart(cart);
                userRepository.save(user);
            }else {
                throw new UserNotFoundException("user not found");
            }
            return cart;
        }else {
            throw new CartNotFoundException("Cart not Found");
        }

    }
}
