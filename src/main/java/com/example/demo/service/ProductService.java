package com.example.demo.service;

import com.example.demo.entity.Product;
import java.util.List;

public interface ProductService {
    Product save(Product product);
    List<Product> findAll();
    List<Product> findAllByCurrentUser();
    Product findById(Long id);
    void deleteById(Long id);
}