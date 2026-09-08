package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CartDTO.CartItemRequestDTO;
import com.example.ecommerce.dto.CartDTO.CartResponseDTO;
import com.example.ecommerce.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDTO requestDTO) {
        CartResponseDTO cartResponse = cartService.addCartItem(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(@PathVariable Long userId, @PathVariable Long productId,
            @RequestBody Integer quantity) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCartItem(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<String> deleteCartItemByProductId(@PathVariable Long userId, @PathVariable Long productId) {
        cartService.deleteCartItemByProductId(userId, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{userId}/items")
    public ResponseEntity<String> deleteCartItemByUserId(@PathVariable Long userId) {
        cartService.deleteCartItemsByUserId(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
