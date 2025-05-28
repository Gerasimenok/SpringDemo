package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false)
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Column
    private String description;

    @Column
    private LocalDate createdDate;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}