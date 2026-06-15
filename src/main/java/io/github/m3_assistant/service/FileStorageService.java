package io.github.m3_assistant.service;

import io.github.m3_assistant.repository.FileAliasRepository;
import io.github.m3_assistant.model.FileAlias;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

@Service
public class FileStorageService {

private final Path filesLocation;  // Папка для документов (uploads/files)
private final FileAliasRepository fileAliasRepository; // Внедряем репозиторий

public FileStorageService(@Value("${app.file.storage-location}") String storageLocation, FileAliasRepository fileAliasRepository) {
    // 1. Инициализируем репозиторий
    this.fileAliasRepository = fileAliasRepository;

    // 2. Инициализируем filesLocation (ИМЕННО ЭТОГО НЕ ХВАТАЕТ)
    this.filesLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
    // Родительская папка (uploads) будет на уровень выше
    // Корень (uploads)
    Path rootLocation = this.filesLocation.getParent();
    try {
        // Создаем иерархию папок
        Files.createDirectories(rootLocation);
        Files.createDirectories(this.filesLocation);
    } catch (IOException e) {
        throw new RuntimeException("Ошибка инициализации хранилища", e);
    }
}

public String getAliasForFile(String filename, String folder) {
    List<FileAlias> aliases = fileAliasRepository.findByOriginalFileName(filename);

    // Используем простой классический подход, который работает везде
    if (aliases != null && !aliases.isEmpty()) {
        return aliases.getFirst().getAliasName();
    }
    return filename;
}

// Загрузка файла
// store принимает имя целевой папки
public void store(MultipartFile file, String targetFolder) throws IOException {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
        throw new IllegalArgumentException("Имя файла не может быть пустым!");
    }
    String filename = StringUtils.cleanPath(originalFilename);

    // Если папка не выбрана или это "Root", пишем в корень
    Path destinationFolder = (targetFolder == null || targetFolder.equals("Root") || targetFolder.isEmpty())
            ? filesLocation
            : filesLocation.resolve(targetFolder).normalize();

    // Защита: проверяем, что папка все еще внутри хранилища
    if (!destinationFolder.startsWith(filesLocation)) {
        throw new SecurityException("Попытка записи вне хранилища!");
    }

    // Создаем папку, если вдруг её нет
    Files.createDirectories(destinationFolder);

    // Копируем файл
    Files.copy(file.getInputStream(), destinationFolder.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
}

// Поиск ресурса в папке /files
public Resource loadAsResource(String folder, String filename) throws MalformedURLException {
    // Определяем путь к папке (если Root, то это корень, иначе - путь к подпапке)
    Path folderPath = folder.equals("Root") ? filesLocation : filesLocation.resolve(folder);

    // Формируем полный путь к файлу
    Path destinationFile = folderPath.resolve(filename).normalize().toAbsolutePath();

    // Безопасность: проверяем, что итоговый путь начинается с нашей корневой директории
    if (!destinationFile.startsWith(filesLocation.toAbsolutePath())) {
        throw new SecurityException("Ошибка доступа: попытка выхода за пределы хранилища!");
    }

    Resource resource = new UrlResource(destinationFile.toUri());

    if (resource.exists() || resource.isReadable()) {
        return resource;
    } else {
        throw new RuntimeException("Файл не найден: " + filename);
    }
}


// Получение списка файлов и папок /files&folders
public Map<String, List<String>> listFilesAndFolders() throws IOException {
    Map<String, List<String>> structure = new TreeMap<>();
// Сначала добавляем "Root" как ключ, чтобы он всегда был в списке
    structure.put("Root", new ArrayList<>());

    // Рекурсивно проходим по всем папкам внутри filesLocation
    try (var stream = Files.walk(filesLocation, 2)) { // глубина 2, чтобы видеть вложенные папки
        stream.forEach(path -> {
            if (Files.isDirectory(path) && !path.equals(filesLocation)) {
                // Если это подпапка, добавляем её как ключ
                String folderName = path.getFileName().toString();
                structure.putIfAbsent(folderName, new ArrayList<>());
            } else if (Files.isRegularFile(path)) {
                // Если файл, определяем, в какой папке он лежит
                Path parent = path.getParent();
                String folderKey = parent.equals(filesLocation) ? "Root" : parent.getFileName().toString();

                structure.computeIfAbsent(folderKey, k -> new ArrayList<>()).add(path.getFileName().toString());
            }
        });
    }
    return structure;
}

public void moveFile(String filename, String sourceFolder, String targetFolder) throws IOException {
    // Определяем пути
    Path sourceDir = sourceFolder.equals("Root") ? filesLocation : filesLocation.resolve(sourceFolder);
    Path targetDir = targetFolder.equals("Root") ? filesLocation : filesLocation.resolve(targetFolder);

    Path sourcePath = sourceDir.resolve(filename);
    Path targetPath = targetDir.resolve(filename);

    // Проверки безопасности
    if (!sourcePath.startsWith(filesLocation) || !targetPath.startsWith(filesLocation)) {
        throw new SecurityException("Попытка доступа за пределы хранилища!");
    }

    // Перемещение
    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
}

// Удаление из папки /files
public void deleteFile(String filename) throws IOException {
    Path file = filesLocation.resolve(filename);
    Files.deleteIfExists(file);
}

public void createFolder(String folderName) throws IOException {
    // Безопасно формируем путь внутри основной папки документов
    Path newFolderPath = filesLocation.resolve(folderName).normalize();

    // Проверяем, что новая папка все еще находится внутри filesLocation
    if (!newFolderPath.startsWith(filesLocation)) {
        throw new SecurityException("Попытка создать папку за пределами хранилища!");
    }

    // Создаем директорию
    Files.createDirectories(newFolderPath);
}

public void renameFolder(String oldName, String newName) throws IOException {
    Path sourcePath = filesLocation.resolve(oldName).normalize();
    Path targetPath = filesLocation.resolve(newName).normalize();

    // Проверки безопасности
    if (!sourcePath.startsWith(filesLocation) || !targetPath.startsWith(filesLocation)) {
        throw new SecurityException("Попытка доступа за пределы хранилища!");
    }
    if (!Files.exists(sourcePath)) {
        throw new IOException("Исходная папка не найдена!");
    }
    if (Files.exists(targetPath)) {
        throw new IOException("Папка с таким именем уже существует!");
    }

    // Переименование
    Files.move(sourcePath, targetPath);
}

public void deleteFolder(String folderName) throws IOException {
    Path folderPath = filesLocation.resolve(folderName).normalize();

    // Проверка безопасности: не позволяем удалять корень
    if (!folderPath.startsWith(filesLocation) || folderPath.equals(filesLocation)) {
        throw new SecurityException("Нельзя удалять корень или файлы вне хранилища!");
    }

    if (Files.exists(folderPath)) {
        // Рекурсивное удаление всей структуры
        Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
}