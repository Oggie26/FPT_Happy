package com.example.inventoryservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @GetMapping
    public List<Map<String, Object>> getAllInventory() {
        return List.of(
                Map.of("product", "Nước suối", "quantity", 100),
                Map.of("product", "Trà đào", "quantity", 50)
        );
    }

    @PostMapping
    public String addInventory() {
        return "✅ Đã cập nhật kho (fake)";
    }
}
