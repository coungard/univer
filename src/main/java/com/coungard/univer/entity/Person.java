package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "person")
@Data
public class Person {

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
}