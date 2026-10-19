package com.coungard.univer.dto;

/**
 * Действие, записанное в журнал аудита (issue #111).
 */
public enum AuditAction {
  REGISTER,
  CREATE,
  UPDATE,
  DELETE,
  STATUS_CHANGE,
  MARK,
  COMPLETE,
  REJECT
}
