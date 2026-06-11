package io.github.m3_assistant.controller;

import io.github.m3_assistant.service.FileStorageService;
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

@Controller
@RequestMapping("/files")
public class FileController {

private final FileStorageService storageService;

public FileController(FileStorageService storageService) {
    this.storageService = storageService;
}

// Список файлов
@GetMapping("/list")
public String listFiles(Model model) throws IOException {
    model.addAttribute("files", storageService.loadAllFiles());
    return "file-list";
}

// Загрузка файла (Post-запрос)
@PostMapping("/upload")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
    storageService.store(file);
    return "redirect:/files/list";
}

// Скачивание
@GetMapping("/download/{filename:.+}")
@ResponseBody
public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws MalformedURLException {
    Resource resource = storageService.loadAsResource(filename);
    // Определяем MIME-тип (например, application/pdf, video/mp4 и т.д.)
    String contentType = "application/octet-stream"; // По умолчанию
    if (filename.endsWith(".pdf")) contentType = "application/pdf";
    else if (filename.endsWith(".mp4")) contentType = "video/mp4";
    else if (filename.endsWith(".docx")) contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    // и так далее для других расширений

    return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
            // Используем "inline", чтобы браузер попытался открыть файл сам
            // или предложил системную программу
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
}

// Удаление (только админы/менеджеры)
@PostMapping("/delete/{filename:.+}")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String deleteFile(@PathVariable String filename) throws IOException {
    storageService.deleteFile(filename);
    return "redirect:/files/list";
}
}