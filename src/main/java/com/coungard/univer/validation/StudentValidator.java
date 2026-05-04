package com.coungard.univer.validation;

import com.coungard.univer.dto.RegisterStudentDto;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentValidator {

    private final UniversityRepository universityRepository;
    private final StudentRepository studentRepository;

    public void validateRegisterData(RegisterStudentDto registerDto) {

        UUID universityId = registerDto.universityId();
        if (universityId != null && !universityRepository.existsById(universityId)) {
            throw new ResourceNotFoundException("Университет с ID " + universityId + " не найден");
        }

        // Проверка уникальности email
        if (registerDto.email() != null && studentRepository.existsByEmail(registerDto.email())) {
            throw new ValidationException("Студент с таким email уже существует: " + registerDto.email());
        }

        // Проверка уникальности username
        if (registerDto.username() != null && studentRepository.existsByUsername(registerDto.username())) {
            throw new ValidationException("Студент с таким логином уже существует: " + registerDto.username());
        }
    }
}
