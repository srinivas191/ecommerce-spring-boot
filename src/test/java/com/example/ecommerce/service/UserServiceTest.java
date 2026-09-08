package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ecommerce.dto.AddressDTO.AddressRequestDTO;
import com.example.ecommerce.dto.UserDTO.UserRequestDTO;
import com.example.ecommerce.dto.UserDTO.UserResponseDTO;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserWithAddress() {
        AddressRequestDTO addressDTO = new AddressRequestDTO("Building 101", "Metropolis", "NY", "10001");
        UserRequestDTO userDTO = new UserRequestDTO("John", "Doe", "john.doe@example.com", addressDTO);

        UserResponseDTO response = userService.createUser(userDTO);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());

        assertNotNull(response.getAddress());
        assertNotNull(response.getAddress().getId());
        assertEquals("Building 101", response.getAddress().getBuildingName());
        assertEquals("Metropolis", response.getAddress().getCity());
        assertEquals("NY", response.getAddress().getState());
        assertEquals("10001", response.getAddress().getZipCode());
    }
}
