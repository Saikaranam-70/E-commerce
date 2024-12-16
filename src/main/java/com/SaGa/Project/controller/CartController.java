package com.SaGa.Project.controller;

import com.SaGa.Project.model.Cart;
import com.SaGa.Project.model.CartItem;
import com.SaGa.Project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<Cart> addItemToCart(@PathVariable("userId") String userId, @RequestBody CartItem cartItem){
        Cart cart = cartService.addItemToCart(userId, cartItem);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable("userId") String userId){
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return cart.map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable("userId") String userId, @RequestBody CartItem cartItem){
        Cart cart = cartService.removeItemFromCart(userId,cartItem);

        if(cart != null){
            return ResponseEntity.ok(cart);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Cart> clearCart(@PathVariable String userId){
        Cart cart = cartService.clearCart(userId);
        if(cart != null){
            return ResponseEntity.ok(cart);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
