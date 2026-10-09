package com.coungard.univer.controller;

import com.coungard.univer.dto.UniversityRequestDto;
import com.coungard.univer.dto.UniversityRequestStatus;
import com.coungard.univer.dto.request.CompleteUniversityRequest;
import com.coungard.univer.service.UniversityRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/university-requests")
@RequiredArgsConstructor
@Tag(name = "UniversityRequests", description = "Заявки студентов на добавление университета")
@SecurityRequirement(name = "bearerAuth")
public class UniversityRequestController {

  private final UniversityRequestService universityRequestService;

  @Operation(
      summary = "Получить заявки с пагинацией",
      description = "Сначала самые давние. Без status — заявки во всех статусах."
  )
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UniversityRequestDto>> getRequests(
      @RequestParam(required = false) UniversityRequestStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
    Page<UniversityRequestDto> requests = universityRequestService.getRequests(status, pageable);
    return ResponseEntity.ok(requests);
  }

  @Operation(
      summary = "Закрыть заявку с указанием университета",
      description = "Университет — только что созданный или уже существующий, если студент его просто не "
          + "нашёл. Он проставляется в профиль автора заявки, если университет там всё ещё не выбран."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Заявка закрыта"),
      @ApiResponse(responseCode = "404", description = "Заявка или университет не найдены"),
      @ApiResponse(responseCode = "422", description = "Заявка уже обработана")
  })
  @PostMapping("/{id}/complete")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UniversityRequestDto> completeRequest(
      @PathVariable UUID id,
      @Valid @RequestBody CompleteUniversityRequest request) {

    UniversityRequestDto completed = universityRequestService.completeRequest(id, request.universityId());
    return ResponseEntity.ok(completed);
  }

  @Operation(
      summary = "Отклонить заявку",
      description = "Не университет, дубль, мусор. Профиль автора заявки не меняется."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Заявка отклонена"),
      @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
      @ApiResponse(responseCode = "422", description = "Заявка уже обработана")
  })
  @PostMapping("/{id}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UniversityRequestDto> rejectRequest(@PathVariable UUID id) {
    UniversityRequestDto rejected = universityRequestService.rejectRequest(id);
    return ResponseEntity.ok(rejected);
  }
}
