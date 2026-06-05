package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.QualificationClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Интерфейс для доступа к таблице классов
public interface QualificationClassRepository extends JpaRepository<QualificationClass, Long> {
// Находит класс в БД по названию (например, "1")
Optional<QualificationClass> findByQualificationClass(String qualificationClass);
}
