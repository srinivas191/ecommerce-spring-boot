package com.example.ecommerce.dto.CartDTO;

import java.math.BigDecimal;
import java.util.List;

import com.example.ecommerce.model.Cart.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {

    private Long id;
    private List<CartItemResponseDTO> cartItems;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private CartStatus cartStatus;
}
