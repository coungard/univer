package com.coungard.univer.mapper;

import com.coungard.univer.dto.FeedbackAdminDto;
import com.coungard.univer.dto.FeedbackDto;
import com.coungard.univer.entity.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

  public FeedbackDto toDto(Feedback feedback) {
    if (feedback == null) {
      return null;
    }
    return FeedbackDto.builder()
        .id(feedback.getId())
        .type(feedback.getType())
        .message(feedback.getMessage())
        .status(feedback.getStatus())
        .createdAt(feedback.getCreatedAt())
        .build();
  }

  public FeedbackAdminDto toAdminDto(Feedback feedback) {
    if (feedback == null) {
      return null;
    }
    return FeedbackAdminDto.builder()
        .id(feedback.getId())
        .type(feedback.getType())
        .message(feedback.getMessage())
        .status(feedback.getStatus())
        .userId(feedback.getUserId())
        .role(feedback.getRole())
        .username(feedback.getUsername())
        .appVersion(feedback.getAppVersion())
        .platform(feedback.getPlatform())
        .osVersion(feedback.getOsVersion())
        .createdAt(feedback.getCreatedAt())
        .updatedAt(feedback.getUpdatedAt())
        .createdBy(feedback.getCreatedBy())
        .updatedBy(feedback.getUpdatedBy())
        .build();
  }
}
