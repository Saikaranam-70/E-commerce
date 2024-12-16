package com.SaGa.Project.service;

import com.SaGa.Project.exception.CartItemNotFoundException;
import com.SaGa.Project.model.CartItem;
import com.SaGa.Project.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService{
    @Autowired
    private CartItemRepository cartItemRepository;
    @Override
    public CartItem addCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public Optional<CartItem> getCartItemById(String id) {
        Optional<CartItem> cartItem = cartItemRepository.findById(id);
        if (cartItem.isPresent()){
            return cartItem;
        }else {
            throw new CartItemNotFoundException("Cart Item Not found with the Id :"+ id);
        }
    }

    @Override
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    @Override
    public void deleteCartItemById(String id) {
        if(!cartItemRepository.existsById(id)){
            throw new CartItemNotFoundException("Cannot delete. Cart Item not found with the ID:" + id);
        }
        cartItemRepository.deleteById(id);
    }

    @Override
    public CartItem updateCartItem(String id, int quantity, BigDecimal price) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(id);
        if(optionalCartItem.isPresent()){
            CartItem cartItem = optionalCartItem.get();
            cartItem.setPrice(price);
            cartItem.setQuantity(quantity);
            return  cartItemRepository.save(cartItem);
        }else {
            throw new CartItemNotFoundException("CartItem not found with id :"+ id);
        }

    }

    @Override
    public List<CartItem> getCartItemByProductId(String id) {
        return cartItemRepository.findCartItemByProductId(id);
    }
}

