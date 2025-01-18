/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0;
 * *****************************************************************************************
 */

package com.app.anyshop.cms.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "product_images")
@AllArgsConstructor
@NoArgsConstructor
public class ProductImages extends Audit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String url;
  private Boolean isThumbnail;
  private String altText;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
