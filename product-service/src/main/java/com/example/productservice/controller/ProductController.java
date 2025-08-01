package com.example.productservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Map<String, Object>> getAllProducts() {
        return List.of(
                Map.of("id", 1, "name", "Nước Suối", "price", 8000),
                Map.of("id", 2, "name", "Trà Đào", "price", 12000)
        );
    }
}
