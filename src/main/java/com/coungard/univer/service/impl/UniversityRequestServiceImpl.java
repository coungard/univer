package com.coungard.univer.service.impl;

import com.coungard.univer.dto.UniversityRequestDto;
import com.coungard.univer.dto.UniversityRequestStatus;
import com.coungard.univer.dto.request.SubmitUniversityRequest;
import com.coungard.univer.entity.Region;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.UniversityRequest;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.UniversityRequestMapper;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.repository.UniversityRequestRepository;
import com.coungard.univer.service.UniversityRequestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UniversityRequestServiceImpl implements UniversityRequestService {

  private final UniversityRequestRepository universityRequestRepository;
  private final StudentRepository studentRepository;
  private final UniversityRepository universityRepository;
  private final RegionRepository regionRepository;
  private final UniversityRequestMapper universityRequestMapper;
  private final TransactionTemplate transactionTemplate;

  @Override
  public UniversityRequestDto submitMyRequest(UUID studentId, SubmitUniversityRequest request) {
    // Транзакции здесь программные: после нарушения уникального индекса транзакция уже непригодна,
    // и уже созданную заявку приходится читать в новой
    try {
      return transactionTemplate.execute(status -> saveMyRequest(studentId, request));
    } catch (DataIntegrityViolationException ex) {
      // Гонка двух запросов одного студента: оба не нашли необработанной заявки, второй INSERT
      // остановил uq_university_request_pending_student. Отдаём заявку, созданную первым
      return transactionTemplate.execute(status -> universityRequestRepository
          .findByStudentIdAndStatus(studentId, UniversityRequestStatus.PENDING)
          .map(universityRequestMapper::toDto)
          .orElseThrow(() -> ex));
    }
  }

  private UniversityRequestDto saveMyRequest(UUID studentId, SubmitUniversityRequest request) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + studentId));

    // Выбор университета закрывает заявку автоматически, поэтому заявка при выбранном университете
    // зависла бы необработанной: сначала университет нужно снять в PATCH /students/me
    if (student.getUniversity() != null) {
      throw new ValidationException("Нельзя оставить заявку, пока в профиле выбран университет");
    }

    Region region = request.regionId() == null
        ? null
        : regionRepository.findById(request.regionId())
            .orElseThrow(() -> new ResourceNotFoundException("Регион не найден с ID: " + request.regionId()));

    UniversityRequest universityRequest = universityRequestRepository
        .findByStudentIdAndStatus(studentId, UniversityRequestStatus.PENDING)
        .orElseGet(() -> {
          UniversityRequest created = new UniversityRequest();
          created.setStudent(student);
          return created;
        });
    universityRequest.setName(request.name().strip());
    universityRequest.setRegion(region);

    // saveAndFlush, а не save: нарушение уникального индекса должно всплыть здесь, а не на коммите
    UniversityRequest saved = universityRequestRepository.saveAndFlush(universityRequest);
    return universityRequestMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityRequestDto getMyRequest(UUID studentId) {
    // Необработанная заявка всегда самая новая: следующую можно оставить только после её закрытия
    UniversityRequest universityRequest = universityRequestRepository
        .findFirstByStudentIdOrderByCreatedAtDesc(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("У студента нет заявок на добавление университета"));
    return universityRequestMapper.toDto(universityRequest);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityRequestDto> getRequests(UniversityRequestStatus status, Pageable pageable) {
    Page<UniversityRequest> requests = status == null
        ? universityRequestRepository.findAll(pageable)
        : universityRequestRepository.findByStatus(status, pageable);
    return requests.map(universityRequestMapper::toDto);
  }

  @Override
  @Transactional
  public UniversityRequestDto completeRequest(UUID id, UUID universityId) {
    UniversityRequest universityRequest = findPendingRequest(id);
    University university = universityRepository.findById(universityId)
        .orElseThrow(() -> new ResourceNotFoundException("Университет не найден с ID: " + universityId));

    universityRequest.setStatus(UniversityRequestStatus.COMPLETED);
    universityRequest.setUniversity(university);

    // Студент мог выбрать университет сам, пока заявка ждала, — тогда его выбор не трогаем
    Student student = universityRequest.getStudent();
    if (student.getUniversity() == null) {
      student.setUniversity(university);
      studentRepository.save(student);
    }

    UniversityRequest saved = universityRequestRepository.save(universityRequest);
    return universityRequestMapper.toDto(saved);
  }

  @Override
  @Transactional
  public UniversityRequestDto rejectRequest(UUID id, String comment) {
    UniversityRequest universityRequest = findPendingRequest(id);
    universityRequest.setStatus(UniversityRequestStatus.REJECTED);
    universityRequest.setComment(StringUtils.hasText(comment) ? comment.strip() : null);

    UniversityRequest saved = universityRequestRepository.save(universityRequest);
    return universityRequestMapper.toDto(saved);
  }

  @Override
  @Transactional
  public void completePendingRequestOnUniversityChosen(Student student) {
    if (student.getUniversity() == null) {
      return;
    }
    universityRequestRepository.findByStudentIdAndStatus(student.getId(), UniversityRequestStatus.PENDING)
        .ifPresent(universityRequest -> {
          universityRequest.setStatus(UniversityRequestStatus.COMPLETED);
          universityRequest.setUniversity(student.getUniversity());
          universityRequestRepository.save(universityRequest);
        });
  }

  private UniversityRequest findPendingRequest(UUID id) {
    UniversityRequest universityRequest = universityRequestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена с ID: " + id));
    if (universityRequest.getStatus() != UniversityRequestStatus.PENDING) {
      throw new ValidationException("Заявка уже обработана");
    }
    return universityRequest;
  }
}
