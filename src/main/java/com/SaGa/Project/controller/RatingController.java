package com.SaGa.Project.controller;

import com.SaGa.Project.model.Product;
import com.SaGa.Project.model.Rating;
import com.SaGa.Project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private ProductService productService;

    @PutMapping("/{productId}")
    public ResponseEntity<Product> addRatingToProduct(@PathVariable("productId") String productId, @RequestBody Rating rating){
        Product updatedProduct = productService.addRatingToProduct(productId, rating);
        return ResponseEntity.ok(updatedProduct);
    }
}
