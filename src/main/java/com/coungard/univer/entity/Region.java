package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Субъект РФ. Справочник, наполняется миграцией (см. V24__insert_regions.sql).
 */
@Entity
@Table(name = "regions")
@NoArgsConstructor
@Data
public class Region {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * Двузначный код субъекта РФ, например "05" — Дагестан.
   */
  @Column(nullable = false, unique = true, length = 2)
  private String code;

  @Column(nullable = false, unique = true)
  private String name;
}
