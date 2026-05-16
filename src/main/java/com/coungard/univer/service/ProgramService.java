package com.coungard.univer.service;

import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.request.CreateProgramRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramService {

  ProgramDto createProgram(CreateProgramRequest createProgramRequest);

  ProgramDto getProgramById(UUID id);

  Page<ProgramDto> getProgramsByFaculty(UUID facultyId, Pageable pageable);

  Page<ProgramDto> getPrograms(Pageable pageable);

  ProgramDto updateProgram(UUID id, CreateProgramRequest createProgramRequest);

  void deleteProgram(UUID id);
}