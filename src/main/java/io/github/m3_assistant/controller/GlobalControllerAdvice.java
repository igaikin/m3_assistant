package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.FileAlias;
import io.github.m3_assistant.repository.FileAliasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

private final FileAliasRepository fileAliasRepository;

@ModelAttribute("aliasMap")
public Map<String, String> getAliasMap() {
    Map<String, String> map = new HashMap<>();
    // Берем все записи, игнорируя папки, так как в базе они null
    List<FileAlias> aliases = fileAliasRepository.findAll();
    for (FileAlias a : aliases) {
        if (a.getOriginalFileName() != null) {
            map.put(a.getOriginalFileName().trim(), a.getAliasName());
        }
    }
    return map;
}
}