package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.FileAlias;
import io.github.m3_assistant.repository.FileAliasRepository;
import io.github.m3_assistant.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

private final FileStorageService storageService;
private final FileAliasRepository fileAliasRepository;

// Список файлов
@GetMapping("/list")
public String listFiles(Model model) throws IOException {
    // Передаем структуру папок
    model.addAttribute("fileStructure", storageService.listFilesAndFolders());

    // Принудительно передаем карту алиасов, чтобы избежать пустых моделей в шаблоне
    model.addAttribute("aliasMap", fileAliasRepository.findAll().stream()
            .collect(java.util.stream.Collectors.toMap(
                    FileAlias::getOriginalFileName,
                    FileAlias::getAliasName,
                    (existing, replacement) -> existing))); // защита от дублей

    return "file-list";
}

// Загрузка файла
@PostMapping("/upload")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String handleFileUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) String aliasName,
        @RequestParam(value = "targetFolder", required = false) String targetFolder) throws IOException {
// Передаем targetFolder в сервис
    storageService.store(file, targetFolder);
    // Если пользователь ввел имя, сохраняем его в БД
    if (aliasName != null && !aliasName.isEmpty()) {
        fileAliasRepository.save(new FileAlias(file.getOriginalFilename(), aliasName, targetFolder));
    }
    return "redirect:/files/list";
}

@PostMapping("/rename-alias")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String renameAlias(@RequestParam String originalFileName,
                          @RequestParam String newAlias) {
    // Folder берется из скрытого пользователь его не вводит!
    List<FileAlias> aliases = fileAliasRepository.findByOriginalFileName(originalFileName);
    FileAlias alias;
    if (aliases.isEmpty()) {
        // Если алиаса еще нет, создаем новый
        alias = new FileAlias();
        alias.setOriginalFileName(originalFileName);
    } else {
        // Если записей несколько — берем первую, остальные удаляем (очистка дублей)
        alias = aliases.get(0);
        if (aliases.size() > 1) {
            fileAliasRepository.deleteAll(aliases.subList(1, aliases.size()));
        }
    }

    alias.setAliasName(newAlias);
    fileAliasRepository.save(alias);

    return "redirect:/files/list";
}

//@ModelAttribute("aliasMap")
//public Map<String, String> getAliasMap() {
//    Map<String, String> map = new HashMap<>();
//    List<FileAlias> aliases = fileAliasRepository.findAll();
//    for (FileAlias a : aliases) {
//        // Ключ: "папка:имяФайла", Значение: "алиас"
//        map.put(a.getTargetFolder() + ":" + a.getOriginalFileName(), a.getAliasName());
//    }
//    return map;
//}

// Скачивание
@GetMapping("/download/{folder}/{filename:.+}")
@ResponseBody
public ResponseEntity<Resource> serveFile(@PathVariable String folder,
                                          @PathVariable String filename) throws MalformedURLException {
    Resource resource = storageService.loadAsResource(folder, filename);
    // Определяем MIME-тип (например, application/pdf, video/mp4 и т.д.)
    String contentType = "application/octet-stream"; // По умолчанию
    if (filename.endsWith(".pdf")) contentType = "application/pdf";
    else if (filename.endsWith(".mp4")) contentType = "video/mp4";
    else if (filename.endsWith(".docx"))
        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    // и так далее для других расширений

    return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
            // Используем "inline", чтобы браузер попытался открыть файл сам
            // или предложил системную программу
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
}

@PostMapping("/move")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String moveFile(@RequestParam String filename,
                       @RequestParam String sourceFolder,
                       @RequestParam String targetFolder) throws IOException {
    storageService.moveFile(filename, sourceFolder, targetFolder);
    return "redirect:/files/list";
}

// Удаление (только админы/менеджеры)
@PostMapping("/delete/{filename:.+}")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String deleteFile(@PathVariable String filename) throws IOException {
    storageService.deleteFile(filename);
    return "redirect:/files/list";
}

@PostMapping("/create-folder")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String createFolder(@RequestParam String folderName) throws IOException {
    // Добавим простую проверку имени папки
    if (!folderName.matches("^[а-яА-Яa-zA-Z0-9_\\\\s-]+$")) {
        throw new IllegalArgumentException("Недопустимое имя папки! Используйте только буквы и цифры.");
    }
    storageService.createFolder(folderName);
    return "redirect:/files/list";
}

@PostMapping("/rename-folder")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String renameFolder(@RequestParam String oldName, @RequestParam String newName) throws IOException {
    if (!newName.matches("^[а-яА-Яa-zA-Z0-9_\\\\s-]+$")) {
        throw new IllegalArgumentException("Недопустимое имя папки! Используйте только буквы и цифры.");
    }
    storageService.renameFolder(oldName, newName);
    return "redirect:/files/list";
}

@PostMapping("/delete-folder")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String deleteFolder(@RequestParam String folderName) throws IOException {
    storageService.deleteFolder(folderName);
    return "redirect:/files/list";
}
}