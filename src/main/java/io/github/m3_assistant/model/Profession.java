package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "professions")
@Data
public class Profession {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String profession; // Название профессии
}