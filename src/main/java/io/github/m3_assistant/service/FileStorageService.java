package io.github.m3_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService {

private final Path rootLocation;   // Корень (uploads)
private final Path filesLocation;  // Папка для документов (uploads/files)

public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
    this.rootLocation = Paths.get(uploadDir);
    this.filesLocation = this.rootLocation.resolve("files");

    try {
        // Создаем иерархию папок
        Files.createDirectories(rootLocation);
        Files.createDirectories(this.filesLocation);
    } catch (IOException e) {
        throw new RuntimeException("Ошибка инициализации хранилища", e);
    }
}

// Загрузка файла строго в папку /files
public void store(MultipartFile file) throws IOException {
    Files.copy(file.getInputStream(), this.filesLocation.resolve(file.getOriginalFilename()),
            StandardCopyOption.REPLACE_EXISTING);
}

// Поиск ресурса в папке /files
public Resource loadAsResource(String filename) throws MalformedURLException {
    Path file = filesLocation.resolve(filename);
    Resource resource = new UrlResource(file.toUri());
    if (resource.exists() || resource.isReadable()) {
        return resource;
    } else {
        throw new RuntimeException("Файл не найден: " + filename);
    }
}

// Получение списка файлов только из папки /files
public List<String> loadAllFiles() throws IOException {
    try (Stream<Path> stream = Files.list(this.filesLocation)) {
        return stream.map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }
}

// Удаление из папки /files
public void deleteFile(String filename) throws IOException {
    Path file = filesLocation.resolve(filename);
    Files.deleteIfExists(file);
}
}