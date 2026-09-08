package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductsId(Long cartId, Long productId);

    void deleteByCartIdAndProductsId(Long cartId, Long productId);

    void deleteByCartId(Long cartId);
}
