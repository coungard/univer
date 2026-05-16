package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "departments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "faculty_id"})
})
@Data
public class Department {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id", nullable = false)
  private Faculty faculty;
}