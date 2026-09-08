package com.example.ecommerce.dto.UserDTO;

import com.example.ecommerce.dto.AddressDTO.AddressResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private AddressResponseDTO address;
}
