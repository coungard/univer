package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_years", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"program_id", "year_number"})
})
@NoArgsConstructor
@Data
public class StudyYear {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id", nullable = false)
  private Program program;

  @Column(name = "year_number", nullable = false)
  private Integer yearNumber;
}
