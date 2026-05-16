package com.coungard.univer.converter;

import com.coungard.univer.dto.StudyDuration;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class StudyIntervalConverter implements AttributeConverter<StudyDuration, String> {

  @Override
  public String convertToDatabaseColumn(StudyDuration attribute) {
    if (attribute == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    if (attribute.getYears() != 0) {
      sb.append(attribute.getYears()).append(" years ");
    }
    if (attribute.getMonths() != 0) {
      sb.append(attribute.getMonths()).append(" months ");
    }
    if (attribute.getHours() != 0) {
      sb.append(attribute.getHours()).append(" hours");
    }
    return sb.toString().trim();
  }

  @Override
  public StudyDuration convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return new StudyDuration(0, 0, 0);
    }
    int years = 0, months = 0, hours = 0;

    // Разбиваем строку по кускам типа "4 years", "10 months", "340 hours"
    String[] parts = dbData.split(" ");
    for (int i = 0; i < parts.length - 1; i += 2) {
      int value = Integer.parseInt(parts[i]);
      String unit = parts[i + 1].toLowerCase();
      if (unit.startsWith("year")) {
        years = value;
      } else if (unit.startsWith("mon")) {
        months = value;
      } else if (unit.startsWith("hour")) {
        hours = value;
      }
    }
    return new StudyDuration(years, months, hours);
  }
}