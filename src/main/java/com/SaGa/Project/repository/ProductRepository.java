package com.SaGa.Project.repository;

import com.SaGa.Project.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ 'category': ?0 }")
    List<Product> findByCategory(String category);

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Product> findByNameContainingIgnoreCase(String name);
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceBetween(BigDecimal maxPrice, BigDecimal maxPrice1);
    @Query("{ 'brand': ?0 }")
    List<Product> findByBrand(String brand);
}
