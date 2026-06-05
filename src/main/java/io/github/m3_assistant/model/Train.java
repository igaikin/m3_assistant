package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trains")
@Data
public class Train {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String train; // Название поезда/состава
}