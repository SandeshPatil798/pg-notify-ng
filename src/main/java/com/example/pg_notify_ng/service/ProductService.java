package com.example.pg_notify_ng.service;

import com.example.pg_notify_ng.model.Product;
import com.example.pg_notify_ng.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional
  public Product createProduct(Product product) {
    // Ensure the ID is null for the save operation to be an INSERT.
    product.setId(null);
    return productRepository.save(product);
  }

  @Transactional(readOnly = true)
  public List<Product> getAllProducts() {
    return StreamSupport.stream(productRepository.findAll().spliterator(), false)
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<Product> getProductById(Integer id) {
    return productRepository.findById(id);
  }

  @Transactional
  public Optional<Product> updateProduct(Integer id, Product productDetails) {
    return productRepository.findById(id)
        .map(existingProduct -> {
          // Since Product is now a mutable class, we can use setters.
          existingProduct.setName(productDetails.getName());
          existingProduct.setDescription(productDetails.getDescription());
          existingProduct.setPrice(productDetails.getPrice());
          // Spring Data JDBC will perform an UPDATE because the 'id' is present.
          return productRepository.save(existingProduct);
        });
  }

  @Transactional
  public boolean deleteProduct(Integer id) {
    if (productRepository.existsById(id)) {
      productRepository.deleteById(id);
      return true;
    }
    return false;
  }
}