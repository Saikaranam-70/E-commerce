package com.SaGa.Project.controller;

import com.SaGa.Project.model.Product;
import com.SaGa.Project.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hashRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@ModelAttribute Product product, @RequestPart("image")MultipartFile image) throws IOException {
        Product savedProduct = productService.addProduct(product, image);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId){

            Optional<Product> product = productService.getProductById(productId);
            if (product.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }else{
                return new ResponseEntity<>(product.get(), HttpStatus.FOUND);
            }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> allProducts = productService.getAllProducts();
        if(!allProducts.isEmpty()){
            return new ResponseEntity<>(allProducts, HttpStatus.FOUND);
        }else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getProductByCategory/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable("category") String category){
        List<Product> categoryProducts = productService.getProductByCategory(category);
        if(!categoryProducts.isEmpty()){
            return new ResponseEntity<>(categoryProducts, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam( value = "query", required = false) String name,
                                                   @RequestParam( value = "brand", required = false) String brand,
                                                        @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                                        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                                        @RequestParam(value = "category", required = false) String category){
        List<Product> searchedProducts = productService.getProductsByFiltering(name, minPrice, maxPrice, category, brand);
        return new ResponseEntity<>(searchedProducts, HttpStatus.OK);

    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getAllCategories(){
        Set<String> categories = productService.getAllUniqueCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/top-discounts")
    public ResponseEntity<List<Product>> getTopDiscountedProducts(@RequestParam(defaultValue = "5") int topN){
        List<Product> topDiscountedProduct = productService.getDiscountedProducts(topN);
        return ResponseEntity.ok(topDiscountedProduct);
    }

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
