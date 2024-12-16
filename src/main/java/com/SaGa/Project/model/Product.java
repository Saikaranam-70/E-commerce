package com.SaGa.Project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.w3c.dom.ls.LSInput;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "Product")
public class Product {
    @Id
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private String category;
    private String brand;
    private int stock;
    private String imageUrl;
    private Map<String, String> attributes;
    private List<Rating> ratings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private Date createdAt;

    public String getId() {
        return productId;
    }


    public void setId(String id) {
        this.productId = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void addRating(Rating rating){
        if(this.ratings == null){
            this.ratings = new ArrayList<>();
        }
        this.ratings.add(rating);
    }

    public double getAverageRating(){
        if(ratings.isEmpty()){
            return 0.0;
        }
        int totalRating = ratings.stream().mapToInt(Rating::getRating).sum();
        return (double) totalRating/ratings.size();
    }

    public void addReview(Review review){
        if(this.reviews == null){
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }

    public BigDecimal getDiscountedPrice(){
        if(discount !=null && discount.compareTo(BigDecimal.ZERO)>0){
            return price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100)));
        }
        return price;
    }

    public static List<Product> getTopDiscountedProducts(List<Product> products, int topN){
        return products.stream()
                .sorted((p1, p2) -> p2.getDiscountedPrice().compareTo(p1.getDiscountedPrice()))
                .limit(topN)
                .toList();
    }
}
