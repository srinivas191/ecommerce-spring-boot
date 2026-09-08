package com.example.ecommerce.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private long id;
    private String name;
    private String colour;
    private Double prize;
    private Long quantity;
    private String imageUrl;
}
