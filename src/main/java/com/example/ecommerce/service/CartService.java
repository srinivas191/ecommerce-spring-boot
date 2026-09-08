package com.example.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;
//import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.CartDTO.CartItemRequestDTO;
import com.example.ecommerce.dto.CartDTO.CartItemResponseDTO;
import com.example.ecommerce.dto.CartDTO.CartResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Cart.CartStatus;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Products;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartResponseDTO addCartItem(Long userId, CartItemRequestDTO requestDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id :" + userId));

        Products products = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with Id :" + requestDTO.getProductId()));

        Cart existingCart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setDiscount(BigDecimal.ZERO);
            cart.setTotalAmount(BigDecimal.ZERO);
            cart.setCartStatus(CartStatus.ACTIVE);
            return cart;
        });

        // Check if product is already present in cart
        Optional<CartItem> existingItemOpt = existingCart.getCartItems().stream()
                .filter(item -> item.getProducts() != null && item.getProducts().getId().equals(products.getId()))
                .findFirst();

        // int currentCartQuantity =
        // existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int currentCartQuantity = 0;
        if (existingItemOpt.isPresent()) {
            currentCartQuantity = existingItemOpt.get().getQuantity();
        }
        int requestedQuantity = requestDTO.getQuantity();
        int newTotalQuantity = currentCartQuantity + requestedQuantity;

        long availableStock = products.getQuantity() != null ? products.getQuantity() : 0L;
        if (newTotalQuantity > availableStock) {
            throw new IllegalArgumentException("Insufficient stock for product '" + products.getName()
                    + "'. Available stock: " + availableStock + ", Requested total: " + newTotalQuantity);
        }

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(newTotalQuantity);
            existingItem.setSubTotal(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newTotalQuantity)));
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(existingCart);
            newItem.setProducts(products);
            newItem.setQuantity(requestedQuantity);

            BigDecimal unitPrice = products.getPrize() != null ? BigDecimal.valueOf(products.getPrize())
                    : BigDecimal.ZERO;
            newItem.setUnitPrice(unitPrice);
            newItem.setSubTotal(unitPrice.multiply(BigDecimal.valueOf(requestedQuantity)));

            existingCart.getCartItems().add(newItem);
        }

        // Calculate total amount for the cart
        // BigDecimal total = existingCart.getCartItems().stream()
        // .map((e) -> {
        // // CartItem::getSubTotal
        // return e.getSubTotal();
        // })
        // .filter(Objects::nonNull)
        // .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : existingCart.getCartItems()) {
            if (item.getSubTotal() != null) {
                total = total.add(item.getSubTotal());
            }
        }

        BigDecimal discount = existingCart.getDiscount() != null ? existingCart.getDiscount() : BigDecimal.ZERO;
        existingCart.setTotalAmount(total.subtract(discount).max(BigDecimal.ZERO));

        Cart cart = cartRepository.save(existingCart);

        return mapToCartResponseDTO(cart);
    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(this::mapToCartResponseDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CartResponseDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::mapToCartResponseDTO)
                .toList();
    }

    public CartResponseDTO updateCartItem(Long userId, Long productId, Integer quantity) {
        // User user = userRepository.findById(userId)
        // .orElseThrow(() -> new ResourceNotFoundException("User not found with Id :" +
        // userId));

        // Products products = productRepository.findById(productId)
        // .orElseThrow(
        // () -> new ResourceNotFoundException("Product not found with Id :" +
        // productId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with Id :" + userId));

        CartItem existingItem = cartItemRepository.findByCartIdAndProductsId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with userId :" + userId + "productId :" + productId));

        existingItem.setQuantity(quantity);
        existingItem.setSubTotal(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            if (item.getSubTotal() != null) {
                total = total.add(item.getSubTotal());
            }
        }

        BigDecimal discount = cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO;
        cart.setTotalAmount(total.subtract(discount).max(BigDecimal.ZERO));

        // Cart cart = cartRepository.save(existingCart);

        return mapToCartResponseDTO(cart);
    }

    @Transactional
    public void deleteCartItemByProductId(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with userId :" + userId));

        cartItemRepository.findByCartIdAndProductsId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with productId :" + productId));

        cartItemRepository.deleteByCartIdAndProductsId(cart.getId(), productId);
    }

    @Transactional
    public void deleteCartItemsByUserId(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with userId :" + userId));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    public CartResponseDTO mapToCartResponseDTO(Cart cart) {
        CartResponseDTO cartResponse = new CartResponseDTO();

        List<CartItemResponseDTO> itemDTOs = cart.getCartItems() == null ? List.of()
                : cart.getCartItems().stream()
                        .map(this::mapToCartItemResponseDTO)
                        .toList();

        cartResponse.setCartItems(itemDTOs);
        cartResponse.setId(cart.getId());
        cartResponse.setTotalAmount(cart.getTotalAmount());
        cartResponse.setDiscount(cart.getDiscount());
        cartResponse.setCartStatus(cart.getCartStatus());

        return cartResponse;
    }

    private CartItemResponseDTO mapToCartItemResponseDTO(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        if (item.getProducts() != null) {
            dto.setProductId(item.getProducts().getId());
            dto.setProductName(item.getProducts().getName());
            dto.setImageUrl(item.getProducts().getImageUrl());
        }
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubTotal());
        return dto;
    }

}
