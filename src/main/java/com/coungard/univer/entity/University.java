package com.coungard.univer.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "universities")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Data
public class University implements Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  private String description;

  private String rector;

  @Column(name = "founding_year")
  private Integer foundingYear;

  @Column(name = "student_count")
  private Integer studentCount;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "address_id",
      foreignKey = @ForeignKey(name = "fk_university_address"),
      unique = true
  )
  private Address address;

  @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Faculty> faculties = new ArrayList<>();

  // Convenience methods to maintain bidirectional relationship
  public void addFaculty(Faculty faculty) {
    faculties.add(faculty);
    faculty.setUniversity(this);
  }

  public void removeFaculty(Faculty faculty) {
    faculties.remove(faculty);
    faculty.setUniversity(null);
  }
}