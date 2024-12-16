package com.SaGa.Project.controller;

import com.SaGa.Project.model.CartItem;
import com.SaGa.Project.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cartItem")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(@RequestBody CartItem cartItem){
        CartItem savedCartItem = cartItemService.addCartItem(cartItem);
        return new ResponseEntity<>(savedCartItem, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CartItem> getCartItemById(@PathVariable("id") String id){
        Optional<CartItem> cartItem = cartItemService.getCartItemById(id);
        return cartItem.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(()-> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CartItem>> getAllCartItems(){
        List<CartItem> cartItems = cartItemService.getAllCartItems();
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deletedCartItem(@PathVariable("id") String id){
        cartItemService.deleteCartItemById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable("id") String id, @RequestParam int quantity, @RequestParam BigDecimal price){
        try{
            CartItem updateCartItem = cartItemService.updateCartItem(id, quantity, price);
            return new ResponseEntity<>(updateCartItem, HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }



}
