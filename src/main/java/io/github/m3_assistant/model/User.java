package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter // Генерирует все геттеры
@Setter // Генерирует все сеттеры
@NoArgsConstructor // Конструктор без аргументов (обязателен для Hibernate)
@AllArgsConstructor // Конструктор со всеми полями
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String avatar;
private String firstName;
private String surname;
private String patronymic;
private String personnelNumber;
private String email;
private String password;

@ManyToOne
@JoinColumn(name = "qualification_class_id")
private QualificationClass qualificationClass;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "profession_id")
private Profession profession;

@ManyToOne
@JoinColumn(name = "role_id")
private Role role;

@ManyToOne
@JoinColumn(name = "train_id")
private Train train;
}