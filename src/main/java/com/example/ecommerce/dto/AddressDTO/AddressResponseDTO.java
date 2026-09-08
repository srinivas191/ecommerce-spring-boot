package com.example.ecommerce.dto.AddressDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {

    private Long id;
    private String buildingName;
    private String city;
    private String state;
    private String zipCode;
}
