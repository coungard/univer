package com.coungard.univer.dto;

/**
 * Статус согласования циклического расписания семестра ({@code WeekScheduleCycle}).
 */
public enum WeekScheduleCycleStatus {

  /** Черновик — расписание ещё формируется, доступно для правок ADMIN и STUDENT своей группы. */
  DRAFT,

  /** Согласовано — расписание финализировано, правки доступны только ADMIN. */
  AGREED
}
