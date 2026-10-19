package com.coungard.univer.service;

/**
 * Сколько занятий пары удалено и создано заново при её изменении или удалении (issue #99) — для
 * журнала аудита (issue #111).
 */
public record PairLecturesChange(int deleted, int created) {

  public static final PairLecturesChange NONE = new PairLecturesChange(0, 0);
}
