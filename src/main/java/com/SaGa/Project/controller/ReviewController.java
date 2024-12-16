package com.SaGa.Project.controller;

import com.SaGa.Project.model.Product;
import com.SaGa.Project.model.Review;
import com.SaGa.Project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ProductService productService;

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateReviewOfProduct(@PathVariable("productId") String productId,@RequestBody Review review){
        Product product = productService.updateReviewOfProduct(productId, review);
        return ResponseEntity.ok(product);
    }
}
