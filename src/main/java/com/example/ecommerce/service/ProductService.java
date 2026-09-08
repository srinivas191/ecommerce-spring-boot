package com.example.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ecommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import com.example.ecommerce.dto.ProductDTO.ProductRequestDTO;
import com.example.ecommerce.dto.ProductDTO.ProductResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Products;

import com.example.ecommerce.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponseDTO> getAllProducts(
            String name,
            String colour,
            Double minPrize,
            Double maxPrize,
            Pageable pageable) {

        Specification<Products> spec = ProductSpecification.getProductsByFilter(name, colour, minPrize, maxPrize);

        return productRepository.findAll(spec, pageable)
                .map(this::convertProductToResponseDto);
    }

    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertProductToResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id:" + id));
    }

    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        return convertProductToResponseDto(productRepository.save(requestDTOtoProduct(requestDTO)));
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        return productRepository.findById(id)
                .map(p -> {
                    p.setName(requestDTO.getName());
                    p.setColour(requestDTO.getColour());
                    p.setPrize(requestDTO.getPrize());
                    p.setQuantity(requestDTO.getQuantity());
                    p.setImageUrl(requestDTO.getImageUrl());
                    productRepository.save(p);

                    return convertProductToResponseDto(p);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id:" + id));
    }

    public void deleteProduct(Long id) {
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id:" + id));

        productRepository.delete(products);

    }

    public ProductResponseDTO convertProductToResponseDto(Products products) {

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(products.getId());
        responseDTO.setName(products.getName());
        responseDTO.setColour(products.getColour());
        responseDTO.setPrize(products.getPrize());
        responseDTO.setQuantity(products.getQuantity());
        responseDTO.setImageUrl(products.getImageUrl());
        return responseDTO;
    }

    public Products requestDTOtoProduct(ProductRequestDTO requestDTO) {

        Products products = new Products();
        products.setName(requestDTO.getName());
        products.setColour(requestDTO.getColour());
        products.setPrize(requestDTO.getPrize());
        products.setQuantity(requestDTO.getQuantity().longValue());
        products.setImageUrl(requestDTO.getImageUrl());

        return products;
    }
}
