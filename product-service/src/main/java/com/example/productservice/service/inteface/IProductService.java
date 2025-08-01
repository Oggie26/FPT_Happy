package com.example.productservice.service.inteface;

import com.example.productservice.entity.Product;
import com.example.productservice.response.ProductResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    ProductResponse addProduct(Product product);
    ProductResponse updateProduct(Product product);
    void deleteProduct(String productId);
    void disableProduct(String productId);
    ProductResponse getProductById(String productId);
    List<ProductResponse> getProducts();
}