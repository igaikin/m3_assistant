package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "qualification_classes")
@Data
public class QualificationClass {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String qualificationClass; // Количество классов
}