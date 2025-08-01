package com.example.employeeservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @GetMapping
    public List<String> getAllEmployees() {
        return List.of("🧑‍💼 Nhân viên A", "👩‍💼 Nhân viên B");
    }

    @PostMapping
    public String addEmployee() {
        return "✅ Đã thêm nhân viên mới (fake)";
    }
}
