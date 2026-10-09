package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_years", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"faculty_id", "year_number"})
})
@NoArgsConstructor
@Data
public class StudyYear {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "faculty_id", nullable = false)
  private UUID facultyId;

  @Column(name = "year_number", nullable = false)
  private Integer yearNumber;
}
