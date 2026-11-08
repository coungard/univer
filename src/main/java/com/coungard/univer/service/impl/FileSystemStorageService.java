package com.coungard.univer.service.impl;

import com.coungard.univer.config.MediaConfig;
import com.coungard.univer.exception.StorageException;
import com.coungard.univer.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link StorageService} на локальном диске (issue #136): объект с ключом {@code a/b/c.jpg} — файл
 * {@code <media.storage-dir>/a/b/c.jpg}.
 */
@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

  private static final Pattern KEY_PATTERN =
      Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]*(/[A-Za-z0-9][A-Za-z0-9._-]*)*");

  /** Временные файлы недописанных объектов. Префикс с точкой не проходит {@link #KEY_PATTERN}. */
  private static final String TEMP_PREFIX = ".upload-";

  private final Path root;
  private final String publicBaseUrl;

  @Autowired
  public FileSystemStorageService(MediaConfig mediaConfig) {
    this(Path.of(mediaConfig.getStorageDir()), mediaConfig.getPublicBaseUrl());
  }

  public FileSystemStorageService(Path root, String publicBaseUrl) {
    this.root = root.toAbsolutePath().normalize();
    this.publicBaseUrl = publicBaseUrl.endsWith("/")
        ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
        : publicBaseUrl;
    try {
      Files.createDirectories(this.root);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось создать каталог медиа-файлов " + this.root, e);
    }
    log.info("Медиа-файлы хранятся в {}, публичный адрес {}", this.root, this.publicBaseUrl);
  }

  @Override
  public void put(String key, InputStream content, String contentType) {
    Path target = resolve(key);
    Path temp = null;
    try {
      Files.createDirectories(target.getParent());
      // Временный файл в том же каталоге — на той же файловой системе, иначе move не будет атомарным
      temp = Files.createTempFile(target.getParent(), TEMP_PREFIX, ".tmp");
      Files.copy(content, temp, StandardCopyOption.REPLACE_EXISTING);
      move(temp, target);
    } catch (IOException e) {
      deleteQuietly(temp);
      throw new StorageException("Не удалось сохранить медиа-файл " + key, e);
    }
  }

  @Override
  public boolean delete(String key) {
    Path target = resolve(key);
    try {
      boolean deleted = Files.deleteIfExists(target);
      deleteEmptyParents(target.getParent());
      return deleted;
    } catch (IOException e) {
      throw new StorageException("Не удалось удалить медиа-файл " + key, e);
    }
  }

  @Override
  public boolean exists(String key) {
    return Files.isRegularFile(resolve(key));
  }

  @Override
  public String publicUrl(String key) {
    validate(key);
    return publicBaseUrl + "/" + key;
  }

  private Path resolve(String key) {
    validate(key);
    Path path = root.resolve(key).normalize();
    // Шаблон ключа уже исключает выход за пределы каталога; проверка — на случай его будущей правки
    if (!path.startsWith(root) || path.equals(root)) {
      throw new IllegalArgumentException("Недопустимый ключ медиа-файла: " + key);
    }
    return path;
  }

  private static void validate(String key) {
    if (key == null || key.length() > MAX_KEY_LENGTH || !KEY_PATTERN.matcher(key).matches()) {
      throw new IllegalArgumentException("Недопустимый ключ медиа-файла: " + key);
    }
  }

  private static void move(Path source, Path target) throws IOException {
    try {
      Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /** Убирает опустевшие каталоги ключа (например, {@code avatars/{fileId}/}), не трогая сам корень. */
  private void deleteEmptyParents(Path dir) {
    while (dir != null && dir.startsWith(root) && !dir.equals(root)) {
      try {
        if (!Files.deleteIfExists(dir)) {
          return;
        }
      } catch (DirectoryNotEmptyException e) {
        return;
      } catch (IOException e) {
        log.warn("Не удалось удалить пустой каталог медиа-файлов {}: {}", dir, e.toString());
        return;
      }
      dir = dir.getParent();
    }
  }

  private static void deleteQuietly(Path path) {
    if (path == null) {
      return;
    }
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      log.warn("Не удалось удалить временный файл {}: {}", path, e.toString());
    }
  }
}
