package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.FileAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAliasRepository extends JpaRepository<FileAlias, Long> {
// Находим запись по имени файла и папке
List<FileAlias> findByOriginalFileName(String originalFileName);
}