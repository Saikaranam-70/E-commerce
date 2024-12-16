package com.SaGa.Project.repository;

import com.SaGa.Project.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findCartItemByProductId(String id);
}
