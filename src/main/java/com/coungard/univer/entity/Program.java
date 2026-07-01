package com.coungard.univer.entity;

import com.coungard.univer.dto.EducationForm;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.Period;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "programs")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Program {

  @Id
  private UUID id;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  @Column(name = "faculty_id")
  private UUID facultyId;

  @Column(name = "code", length = 36)
  private String code;

  @Column(name = "name", length = 72)
  private String name;

  @Column(name = "profession", length = 120)
  private String profession;

  @Column(name = "direction", length = 240)
  private String direction;

  @Column(name = "education_level", length = 80)
  private String educationLevel;

  @Column(name = "education_form", length = 32)
  @Enumerated(EnumType.STRING)
  private EducationForm educationForm;

  @Column(name = "duration_of_study", columnDefinition = "INTERVAL")
  private Period durationOfStudy;

  @Column(name = "qualification", length = 120)
  private String qualification;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}