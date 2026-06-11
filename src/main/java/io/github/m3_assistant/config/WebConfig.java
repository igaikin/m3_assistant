package io.github.m3_assistant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Эта строка говорит Spring:
    // "Если кто-то просит файл по адресу /uploads/**,
    // ищи его в папке uploads/ в корне проекта"
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
}
}