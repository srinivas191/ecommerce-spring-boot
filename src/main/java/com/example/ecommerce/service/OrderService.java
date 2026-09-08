package com.example.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Products;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.dto.CartDTO.CartItemResponseDTO;
import com.example.ecommerce.dto.CartDTO.CartResponseDTO;
import com.example.ecommerce.dto.OrdersDTO.OrderItemResponseDTO;
import com.example.ecommerce.dto.OrdersDTO.OrderResponseDTO;
import com.example.ecommerce.dto.ProductDTO.ProductResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDTO createOrder(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id:" + userId));
        CartResponseDTO cart = cartService.getCartByUserId(userId);

        Order order = new Order();

        if (cart == null || cart.getCartItems() == null) {
            throw new ResourceNotFoundException("No items present in the cart");
        } else {
            List<CartItemResponseDTO> cartItems = cart.getCartItems();
            List<CartItemResponseDTO> outOfStockItems = new ArrayList<>();
            for (CartItemResponseDTO responseDTO : cartItems) {

                ProductResponseDTO product = productService.getProductById(responseDTO.getProductId());
                if (responseDTO.getQuantity() > product.getQuantity()) {
                    outOfStockItems.add(responseDTO);
                }
            }

            if (!outOfStockItems.isEmpty()) {
                throw new IllegalArgumentException("Stock insufficient for products: " + outOfStockItems);
            }

            order.setUser(user);
            order.setStatus(Order.OrderStatus.PENDING);
            order.setTotalAmount(cart.getTotalAmount());

            orderRepository.save(order);

            for (CartItemResponseDTO responseDTO : cartItems) {
                OrderItem orderItem = new OrderItem();

                Products product = productRepository.findById(responseDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with Id:" + responseDTO.getProductId()));

                orderItem.setOrder(order);
                orderItem.setAmount(responseDTO.getSubtotal());
                orderItem.setPrice(BigDecimal.valueOf(product.getPrize()));
                orderItem.setProducts(product);
                orderItem.setQuantity(responseDTO.getQuantity());

                product.setQuantity(product.getQuantity() - responseDTO.getQuantity());

                orderItemRepository.save(orderItem);
            }

            cartService.deleteCartItemsByUserId(userId);
        }
        return mapToOrderResponseDTO(order);
    }

    public OrderResponseDTO getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not present with orderId:" + orderId));

        return mapToOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponseDTO).toList();
    }

    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderId:" + orderId));

        if (order.getStatus() == Order.OrderStatus.PENDING || order.getStatus() == Order.OrderStatus.CONFIRMED) {

            order.setStatus(Order.OrderStatus.CANCELED);

            orderItemRepository.findByOrder_OrderId(orderId).forEach(item -> {
                Products product = item.getProducts();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            });

            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order cannot be canceled in its current state: " + order.getStatus());
        }

        return mapToOrderResponseDTO(order);

    }

    public OrderResponseDTO mapToOrderResponseDTO(Order order) {

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setUserId(order.getUser().getId());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponseDTO> items = orderItemRepository.findByOrder_OrderId(order.getOrderId()).stream()
                .map(this::mapToOrderItemResponseDTO).toList();

        responseDTO.setOrderItems(items);

        return responseDTO;
    }

    public OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem orderItem) {

        OrderItemResponseDTO items = new OrderItemResponseDTO();

        items.setOrderItemId(orderItem.getOrderItemId());
        items.setProductId(orderItem.getProducts().getId());
        items.setProductName(orderItem.getProducts().getName());
        items.setQuantity(orderItem.getQuantity());
        items.setPrice(orderItem.getPrice());
        items.setAmount(orderItem.getAmount());

        return items;
    }

}
