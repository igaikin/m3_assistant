package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String role; // Название роли (ADMIN, USER и т.д.)
}