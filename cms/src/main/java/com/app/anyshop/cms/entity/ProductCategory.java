package com.app.anyshop.cms.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_categories")
public class ProductCategory extends Audit {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToMany(mappedBy = "productCategory")
  private List<Product> products;
}
