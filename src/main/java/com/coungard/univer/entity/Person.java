package com.coungard.univer.entity;

import com.coungard.univer.dto.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "persons")
@Data
public class Person extends AuditableEntity {

  @Id
  private UUID id;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  @Column(name = "username")
  private String username;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @Column(name = "fullname")
  private String fullname;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Column(name = "birthday")
  private LocalDate birthday;

  /** Пол (issue #139); {@code null} — не указан. */
  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 16)
  private Gender gender;

  /**
   * Фото профиля (issue #137). Для ссылок достаточно id файла — у ленивого прокси он есть без запроса к БД.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "avatar_file_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private MediaFile avatarFile;
}