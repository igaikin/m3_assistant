package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "file_alias")
@Getter // Генерирует все геттеры
@Setter // Генерирует все сеттеры
@NoArgsConstructor // Конструктор без аргументов (обязателен для Hibernate)
@AllArgsConstructor
public class FileAlias {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String originalFileName; // Имя, которое лежит на диске
private String aliasName;    // Имя, которое видит пользователь
private String targetFolder;   // В какой папке лежит

public FileAlias(String originalFileName, String aliasName, String targetFolder) {
    this.originalFileName = originalFileName;
    this.aliasName = aliasName;
    this.targetFolder = targetFolder;
}
}
