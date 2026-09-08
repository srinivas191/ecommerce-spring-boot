package com.example.ecommerce.dto.OrdersDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.ecommerce.model.Order.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> orderItems;

}
