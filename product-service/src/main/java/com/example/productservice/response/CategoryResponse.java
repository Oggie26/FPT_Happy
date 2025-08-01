package com.example.productservice.response;

import com.example.productservice.enums.EnumStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    Long id;
    String categoryName;
    String description;
    @Enumerated(EnumType.STRING)
    private EnumStatus status;
}
