package com.SaGa.Project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Cart")
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<CartItem> items;
    private BigDecimal totalPrice;
    private Date createdAt;

    public Cart(String id, String userId, List<CartItem> items, BigDecimal totalPrice, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public Cart() {
        this.items = new ArrayList<>();
        this.totalPrice = BigDecimal.ZERO;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void addItem(CartItem item){
        this.items.add(item);
        recalculateTotalPrice();
    }

    public void removeItem(CartItem item){
        this.items.remove(item);
        recalculateTotalPrice();
    }

    private void recalculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
