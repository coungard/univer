package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.exception.StorageException;
import com.coungard.univer.service.impl.FileSystemStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemStorageServiceTest {

  private static final String KEY = "avatars/0b6f7c1e-2d4a-4f3b-9c8e-1a2b3c4d5e6f/512.jpg";

  @TempDir
  Path root;

  private FileSystemStorageService storage;

  @BeforeEach
  void setUp() {
    storage = new FileSystemStorageService(root, "https://api.example.ru/media/");
  }

  @Test
  void shouldPutObjectCreatingDirectories() throws IOException {
    storage.put(KEY, bytes("jpeg"), "image/jpeg");

    assertThat(storage.exists(KEY)).isTrue();
    assertThat(Files.readString(root.resolve(KEY))).isEqualTo("jpeg");
  }

  @Test
  void shouldReplaceExistingObjectAndLeaveNoTempFiles() throws IOException {
    storage.put(KEY, bytes("old"), "image/jpeg");
    storage.put(KEY, bytes("new"), "image/jpeg");

    assertThat(Files.readString(root.resolve(KEY))).isEqualTo("new");
    try (Stream<Path> files = Files.list(root.resolve(KEY).getParent())) {
      assertThat(files).containsExactly(root.resolve(KEY));
    }
  }

  @Test
  void shouldDeleteObjectAndItsEmptyDirectoriesButKeepRoot() {
    storage.put(KEY, bytes("jpeg"), "image/jpeg");

    assertThat(storage.delete(KEY)).isTrue();

    assertThat(storage.exists(KEY)).isFalse();
    assertThat(root.resolve("avatars")).doesNotExist();
    assertThat(root).isDirectory();
  }

  @Test
  void shouldKeepDirectoryWithOtherObjectsOnDelete() {
    String thumbnail = KEY.replace("512.jpg", "128.jpg");
    storage.put(KEY, bytes("big"), "image/jpeg");
    storage.put(thumbnail, bytes("small"), "image/jpeg");

    storage.delete(KEY);

    assertThat(storage.exists(thumbnail)).isTrue();
  }

  @Test
  void shouldReturnFalseWhenDeletingMissingObject() {
    assertThat(storage.delete("avatars/missing/512.jpg")).isFalse();
  }

  @Test
  void shouldListObjectsUnderPrefixSkippingTempAndForeignFiles() throws IOException {
    String thumbnail = KEY.replace("512.jpg", "128.jpg");
    storage.put(KEY, bytes("big"), "image/jpeg");
    storage.put(thumbnail, bytes("small"), "image/jpeg");
    storage.put("other/file.txt", bytes("x"), "text/plain");
    Files.writeString(root.resolve(KEY).resolveSibling(".upload-123.tmp"), "half");
    Files.writeString(root.resolve("avatars").resolve("чужой файл.txt"), "x");

    assertThat(storage.list("avatars/")).extracting(StorageService.StoredObject::key)
        .containsExactlyInAnyOrder(KEY, thumbnail);
    assertThat(storage.list("avatars/")).allSatisfy(object -> assertThat(object.lastModified()).isNotNull());
    assertThat(storage.list("")).hasSize(3);
    assertThat(storage.list("missing/")).isEmpty();
    assertThatThrownBy(() -> storage.list("avatars")).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> storage.list("../")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldBuildPublicUrlWithoutDoubleSlash() {
    assertThat(storage.publicUrl(KEY)).isEqualTo("https://api.example.ru/media/" + KEY);
  }

  @Test
  void shouldRejectKeysOutsideStorageOrWithUnsafeCharacters() {
    for (String key : new String[] {
        "../secret.jpg", "avatars/../../secret.jpg", "/etc/passwd", "avatars//512.jpg", "avatars/.hidden",
        "avatars\\512.jpg", "avatars/ 512.jpg", "аватар.jpg", "avatars/", "", "a".repeat(513)}) {
      assertThatThrownBy(() -> storage.put(key, bytes("x"), "image/jpeg"))
          .as(key)
          .isInstanceOf(IllegalArgumentException.class);
      assertThatThrownBy(() -> storage.delete(key)).as(key).isInstanceOf(IllegalArgumentException.class);
      assertThatThrownBy(() -> storage.publicUrl(key)).as(key).isInstanceOf(IllegalArgumentException.class);
    }
    assertThatThrownBy(() -> storage.exists(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRemoveTempFileWhenWriteFails() throws IOException {
    InputStream failing = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("обрыв соединения");
      }
    };

    assertThatThrownBy(() -> storage.put(KEY, failing, "image/jpeg")).isInstanceOf(StorageException.class);

    assertThat(storage.exists(KEY)).isFalse();
    try (Stream<Path> files = Files.walk(root)) {
      assertThat(files.filter(Files::isRegularFile)).isEmpty();
    }
  }

  private static byte[] bytes(String content) {
    return content.getBytes(StandardCharsets.UTF_8);
  }
}
