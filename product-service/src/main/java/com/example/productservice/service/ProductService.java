package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.response.ProductResponse;
import com.example.productservice.service.inteface.IProductService;

import java.util.List;


public class ProductService implements IProductService {
    @Override
    public ProductResponse addProduct(Product product) {
        return null;
    }

    @Override
    public ProductResponse updateProduct(Product product) {
        return null;
    }

    @Override
    public void deleteProduct(String productId) {

    }

    @Override
    public void disableProduct(String productId) {

    }

    @Override
    public ProductResponse getProductById(String productId) {
        return null;
    }

    @Override
    public List<ProductResponse> getProducts() {
        return List.of();
    }
}
