package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.AvatarDto;
import com.coungard.univer.dto.MediaFileStatus;
import com.coungard.univer.dto.MediaPurpose;
import com.coungard.univer.entity.MediaFile;
import com.coungard.univer.entity.Person;
import com.coungard.univer.exception.MediaRejectedException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.MediaFileRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.AvatarImageProcessor;
import com.coungard.univer.service.AvatarImageProcessor.ProcessedAvatar;
import com.coungard.univer.service.AvatarKeys;
import com.coungard.univer.service.AvatarService;
import com.coungard.univer.service.AvatarUrls;
import com.coungard.univer.service.MediaFileCleanup;
import com.coungard.univer.service.StorageService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

  private static final int MAX_ORIGINAL_NAME_LENGTH = 255;

  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final MediaFileRepository mediaFileRepository;
  private final StorageService storageService;
  private final AvatarImageProcessor imageProcessor;
  private final AvatarUrls avatarUrls;
  private final MediaFileCleanup mediaFileCleanup;
  private final AuditService auditService;
  private final TransactionTemplate transactionTemplate;

  /** Чей это профиль — для журнала аудита. */
  private record Owner(Person person, AuditEntityType type) {
  }

  @Override
  public AvatarDto upload(UUID userId, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ValidationException("Файл не передан");
    }
    if (file.getSize() > AvatarImageProcessor.MAX_FILE_BYTES) {
      throw MediaRejectedException.tooLarge("Файл больше 5 МБ");
    }
    // Профиль проверяется до обработки, чтобы не тратить на неё CPU зря; в транзакции — ещё раз
    findOwner(userId);
    ProcessedAvatar processed = imageProcessor.process(readBytes(file));
    return transactionTemplate.execute(status -> save(userId, originalName(file), processed));
  }

  private AvatarDto save(UUID userId, String originalName, ProcessedAvatar processed) {
    Owner owner = findOwner(userId);
    UUID fileId = UUID.randomUUID();

    mediaFileCleanup.deleteOnRollback(AvatarKeys.all(fileId));
    storageService.put(AvatarKeys.full(fileId), processed.full(), AvatarImageProcessor.CONTENT_TYPE);
    storageService.put(AvatarKeys.thumbnail(fileId), processed.thumbnail(), AvatarImageProcessor.CONTENT_TYPE);

    MediaFile avatar = new MediaFile();
    avatar.setId(fileId);
    avatar.setPurpose(MediaPurpose.AVATAR);
    avatar.setStorageKey(AvatarKeys.full(fileId));
    avatar.setContentType(AvatarImageProcessor.CONTENT_TYPE);
    avatar.setSizeBytes(processed.full().length);
    avatar.setOriginalName(originalName);
    avatar.setOwnerId(userId);
    avatar.setStatus(MediaFileStatus.READY);
    avatar = mediaFileRepository.save(avatar);

    MediaFile previous = owner.person().getAvatarFile();
    owner.person().setAvatarFile(avatar);
    Map<String, Object> details = AuditService.details("avatarFileId", fileId);
    if (previous != null) {
      details.put("previousAvatarFileId", previous.getId());
      removeFile(previous);
    }
    auditService.record(AuditAction.UPDATE, owner.type(), userId, details);

    return avatarUrls.toDto(avatar);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    Owner owner = findOwner(userId);
    MediaFile avatar = owner.person().getAvatarFile();
    if (avatar == null) {
      return;
    }
    owner.person().setAvatarFile(null);
    removeFile(avatar);
    auditService.record(AuditAction.UPDATE, owner.type(), userId, AuditService.details(
        "avatarRemoved", true,
        "previousAvatarFileId", avatar.getId()));
  }

  @Override
  @Transactional
  public boolean deleteOfRemovedPerson(MediaFile avatar) {
    if (avatar == null) {
      return false;
    }
    removeFile(avatar);
    return true;
  }

  /** Строка — в текущей транзакции (Hibernate удаляет её после обновления ссылки в persons), файлы — после коммита. */
  private void removeFile(MediaFile file) {
    UUID fileId = file.getId();
    mediaFileRepository.deleteById(fileId);
    mediaFileCleanup.deleteAfterCommit(AvatarKeys.all(fileId));
  }

  private Owner findOwner(UUID userId) {
    return studentRepository.findById(userId)
        .map(student -> new Owner(student.getPerson(), AuditEntityType.STUDENT))
        .or(() -> teacherRepository.findById(userId)
            .map(teacher -> new Owner(teacher.getPerson(), AuditEntityType.TEACHER)))
        .orElseThrow(() -> new ResourceNotFoundException("Профиль пользователя не найден"));
  }

  private static byte[] readBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось прочитать загруженный файл", e);
    }
  }

  private static String originalName(MultipartFile file) {
    String name = file.getOriginalFilename();
    if (name == null || name.isBlank()) {
      return null;
    }
    return name.length() > MAX_ORIGINAL_NAME_LENGTH ? name.substring(0, MAX_ORIGINAL_NAME_LENGTH) : name;
  }
}
