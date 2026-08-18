package com.coungard.univer.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "students")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Student implements Auditable {

  @Id
  private UUID id;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "enrollment_date", nullable = false)
  private LocalDate enrollmentDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "university_id", nullable = false)
  private University university;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  private Group group;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  private Person person;
}