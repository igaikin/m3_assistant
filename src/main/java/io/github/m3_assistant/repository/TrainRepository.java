package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Интерфейс для доступа к таблице ролей
public interface TrainRepository extends JpaRepository<Train, Long> {
// Находит состав в БД по названию (например, "10101-10901")
Optional<Train> findByTrain(String train);
}