package com.example.orderservice.feign;

import com.example.orderservice.response.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/api/employees/{id}")
    EmployeeResponse getEmployeeById(@PathVariable("id") Long id);
}



