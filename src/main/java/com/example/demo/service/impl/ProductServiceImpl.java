package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    public Product save(Product product) {
        log.debug("Saving product: {}", product);
        if (product.getUser() == null) {
            product.setUser(userService.getCurrentUser());
        }
        Product savedProduct = productRepository.save(product);
        log.info("Saved product: {}", savedProduct);
        return savedProduct;
    }

    @Override
    public List<Product> findAll() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    public List<Product> findAllByCurrentUser() {
        log.debug("Fetching products for current user");
        var currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.findAll().stream()
                .filter(product -> product.getUser().getId().equals(currentUser.getId()))
                .toList();
        log.info("Found {} products for current user", products.size());
        return products;
    }

    @Override
    public Product findById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting product with id: {}", id);
        productRepository.deleteById(id);
        log.info("Deleted product with id: {}", id);
    }
}