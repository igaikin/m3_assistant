package io.github.m3_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Главная аннотация, которая включает автоконфигурацию и сканирование компонентов Spring
@SpringBootApplication
public class M3AssistantApplication {

// Стандартный метод main, с которого начинается выполнение любого Java-приложения
public static void main(String[] args) {
    // Запуск контейнера Spring Boot
    SpringApplication.run(M3AssistantApplication.class, args);
}
}