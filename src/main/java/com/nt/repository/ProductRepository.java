package com.nt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nt.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Additional query methods (if needed) can be defined here


    public Optional<Product> findProductByName(String name);

    public List<Product>  fetchAllProductsByPriceRange(Double start,Double end);
}
