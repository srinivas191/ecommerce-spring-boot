package com.example.ecommerce.dto.UserDTO;

import com.example.ecommerce.dto.AddressDTO.AddressRequestDTO;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "FirstName is Requires")
    private String firstName;
    @NotBlank(message = "LastName is Requires")
    private String lastName;
    @Column(nullable = false, unique = true)
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotNull(message = "Address is required")
    @Valid
    private AddressRequestDTO address;
}
