package com.SaGa.Project.service;

import com.SaGa.Project.exception.ProductNotFoundException;
import com.SaGa.Project.model.Product;
import com.SaGa.Project.model.Rating;
import com.SaGa.Project.model.Review;
import com.SaGa.Project.repository.ProductRepository;
import com.SaGa.Project.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Product addProduct(Product product, MultipartFile image) throws IOException {

        String imageUrl = uploadImage(image);
        product.setImageUrl(imageUrl);
        product.setCreatedAt(new Date());

        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(String productId) {
        return Optional.ofNullable(productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found")));
    }

    @Override
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getProductsByFiltering(String name, BigDecimal minPrice, BigDecimal maxPrice, String category, String brand) {
        if (name != null && !name.isEmpty()){
            return productRepository.findByNameContainingIgnoreCase(name);
        } else if (minPrice != null && maxPrice !=null) {
            return productRepository.findByPriceBetween(maxPrice, maxPrice);
        } else if (category !=null && !category.isEmpty()) {
            return productRepository.findByCategory(category);
        } else if (brand!=null && !brand.isEmpty()) {
            return productRepository.findByBrand(brand);
        }
        return List.of();
    }



    @Override
    public double getAverageRating(String productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            return product.getAverageRating();
        }else {
            throw new ProductNotFoundException("Product not found with Id :"+productId);
        }
    }

    @Override
    public Product addRatingToProduct(String productId, Rating rating) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()){
            Product product = optionalProduct.get();

            rating.setProductId(productId);
            rating.setCreatedAt(new Date());
            product.addRating(rating);

            return productRepository.save(product);

        }else {
            throw new ProductNotFoundException("Product not found");
        }
    }

    @Override
    public Product updateReviewOfProduct(String productId, Review review) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Review newReview = new Review();
            newReview.setReview(review.getReview());
            newReview.setProductId(productId);
            System.out.println("getting UserId"+review.getUserId());
            newReview.setCreatedAt(new Date());
            newReview.setUserId(review.getUserId());
            product.addReview(newReview);
            return productRepository.save(product);
        }else {
            throw new ProductNotFoundException("Product Not Found with Id :"+productId);
        }
    }

    @Override
    public Set<String> getAllUniqueCategories() {
        List<Product> products = productRepository.findAll();
        Set<String> uniqueCategories = new HashSet<>();

        for (Product product : products){
            if (product.getCategory() != null){
                uniqueCategories.add(product.getCategory());
            }
        }
        return uniqueCategories;
    }

    @Override
    public List<Product> getDiscountedProducts(int topN) {
        List<Product> products = productRepository.findAll();
        return Product.getTopDiscountedProducts(products, topN);
    }

    private String uploadImage(MultipartFile image) throws IOException {

        String fileName = UUID.randomUUID().toString() + "_"+ image.getOriginalFilename();

        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());
        image.transferTo(filePath);

        return fileName;
    }
}
