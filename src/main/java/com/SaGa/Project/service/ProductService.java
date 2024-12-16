package com.SaGa.Project.service;

import com.SaGa.Project.model.Product;
import com.SaGa.Project.model.Rating;
import com.SaGa.Project.model.Review;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface ProductService {
    Product addProduct(Product product, MultipartFile image) throws IOException;

    Optional<Product> getProductById(String productId);

    List<Product> getAllProducts();

    List<Product> getProductByCategory(String category);

    List<Product> getProductsByFiltering(String name, BigDecimal minPrice, BigDecimal maxPrice, String category, String brand);


    double getAverageRating(String productId);

    Product addRatingToProduct(String productId, Rating rating);

    Product updateReviewOfProduct(String productId, Review review);

    Set<String> getAllUniqueCategories();

    List<Product> getDiscountedProducts(int topN);
}
