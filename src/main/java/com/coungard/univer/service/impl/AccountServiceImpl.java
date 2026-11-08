package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.entity.MediaFile;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.AccountDeletionException;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.EnrollmentRepository;
import com.coungard.univer.repository.FeedbackRepository;
import com.coungard.univer.repository.LectureAttendanceRepository;
import com.coungard.univer.repository.LectureRepository;
import com.coungard.univer.repository.LectureSelfAttendanceRepository;
import com.coungard.univer.repository.PairRepository;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.repository.UniversityRequestRepository;
import com.coungard.univer.security.KeycloakAdminService;
import com.coungard.univer.service.AccountService;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.AvatarService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Связанные записи удаляются и отвязываются явно, а не через {@code ON DELETE} в схеме: так поведение не
 * зависит от того, создана схема миграциями или Hibernate (в тестах — {@code create-drop}).
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final LectureAttendanceRepository lectureAttendanceRepository;
  private final LectureSelfAttendanceRepository lectureSelfAttendanceRepository;
  private final UniversityRequestRepository universityRequestRepository;
  private final PairRepository pairRepository;
  private final LectureRepository lectureRepository;
  private final CourseRepository courseRepository;
  private final FeedbackRepository feedbackRepository;
  private final KeycloakAdminService keycloakAdminService;
  private final AuditService auditService;
  private final AvatarService avatarService;

  @Override
  @Transactional
  public void deleteAccount(UUID userId) {
    int feedbackAnonymized = feedbackRepository.anonymizeAuthor(userId);
    studentRepository.findById(userId).ifPresent(student -> deleteStudent(student, feedbackAnonymized));
    teacherRepository.findById(userId).ifPresent(teacher -> deleteTeacher(teacher, feedbackAnonymized));

    // Ошибки ограничений БД должны всплыть до удаления в Keycloak, а не на коммите после него
    studentRepository.flush();

    try {
      keycloakAdminService.deleteUserIfExists(userId.toString());
    } catch (RuntimeException e) {
      throw new AccountDeletionException(
          "Не удалось удалить аккаунт: сервер авторизации недоступен. Данные не изменены, попробуйте позже", e);
    }
  }

  private void deleteStudent(Student student, int feedbackAnonymized) {
    UUID id = student.getId();
    int enrollments = enrollmentRepository.deleteAllOfStudent(id);
    int attendance = lectureAttendanceRepository.deleteAllOfStudent(id);
    int selfAttendance = lectureSelfAttendanceRepository.deleteAllOfStudent(id);
    int universityRequests = universityRequestRepository.deleteAllOfStudent(id);
    MediaFile avatar = student.getPerson().getAvatarFile();
    // Person удаляется каскадом (CascadeType.ALL); аватар — после него: persons ссылается на файл
    studentRepository.delete(student);
    boolean avatarDeleted = avatarService.deleteOfRemovedPerson(avatar);

    auditService.record(AuditAction.DELETE, AuditEntityType.STUDENT, id, AuditService.details(
        "selfDeleted", true,
        "enrollmentsDeleted", enrollments,
        "attendanceDeleted", attendance,
        "selfAttendanceDeleted", selfAttendance,
        "universityRequestsDeleted", universityRequests,
        "avatarDeleted", avatarDeleted,
        "feedbackAnonymized", feedbackAnonymized));
  }

  private void deleteTeacher(Teacher teacher, int feedbackAnonymized) {
    UUID id = teacher.getId();
    Instant now = Instant.now();
    int pairs = pairRepository.detachTeacher(id, now, id);
    int lectures = lectureRepository.detachTeacher(id, now, id);
    int courses = courseRepository.detachTeacher(id, now, id);
    MediaFile avatar = teacher.getPerson().getAvatarFile();
    // Person удаляется каскадом (CascadeType.ALL); аватар — после него: persons ссылается на файл
    teacherRepository.delete(teacher);
    boolean avatarDeleted = avatarService.deleteOfRemovedPerson(avatar);

    auditService.record(AuditAction.DELETE, AuditEntityType.TEACHER, id, AuditService.details(
        "selfDeleted", true,
        "pairsDetached", pairs,
        "lecturesDetached", lectures,
        "coursesDetached", courses,
        "avatarDeleted", avatarDeleted,
        "feedbackAnonymized", feedbackAnonymized));
  }
}
