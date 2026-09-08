package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.UserDTO.UserRequestDTO;
import com.example.ecommerce.dto.UserDTO.UserResponseDTO;
import com.example.ecommerce.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.trace("Request received to fetch all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        log.info("Successfully fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Request received to create user with email: {}", requestDTO.getEmail());
        UserResponseDTO responseDTO = userService.createUser(requestDTO);
        log.info("User successfully created with email: {}", responseDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Request received to fetch user by id: {}", id);
        UserResponseDTO responseDTO = userService.getUserById(id);
        log.info("Successfully fetched user with id: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Request received to update user with id: {}", id);
        UserResponseDTO responseDTO = userService.updateUser(id, requestDTO);
        log.info("Successfully updated user with id: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Request received to delete user with id: {}", id);
        userService.deleteUser(id);
        log.info("Successfully deleted user with id: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
