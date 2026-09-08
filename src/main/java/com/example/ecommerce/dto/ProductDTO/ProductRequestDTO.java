package com.example.ecommerce.dto.ProductDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
    private String colour;
    @Positive(message = "Prize cant not be less than Zero")
    private Double prize;
    @Min(value = 1, message = "Quantity should be greater than 1")
    private Long quantity;
    private String imageUrl;
}
