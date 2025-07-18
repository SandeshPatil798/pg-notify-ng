package com.example.pg_notify_ng.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("products")
public class Product {

  @Id
  private Integer id;

  private String name;

  private String description;

  private BigDecimal price;

  @ReadOnlyProperty
  private Instant createdAt;
}