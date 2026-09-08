package com.example.ecommerce.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.ecommerce.model.Products;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Products> getProductsByFilter(
            String name,
            String colour,
            Double minPrize,
            Double maxPrize) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (colour != null && !colour.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("colour")), colour.toLowerCase()));
            }

            if (minPrize != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("prize"), minPrize));
            }

            if (maxPrize != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("prize"), maxPrize));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
