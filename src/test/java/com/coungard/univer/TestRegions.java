package com.coungard.univer;

import com.coungard.univer.entity.Region;
import com.coungard.univer.repository.RegionRepository;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Регион для тестовых университетов: universities.region_id обязателен (issue #80).
 */
public final class TestRegions {

  private static final AtomicInteger COUNTER = new AtomicInteger();

  private TestRegions() {
  }

  /**
   * Сохраняет новый регион с уникальными кодом и названием.
   */
  public static Region create(RegionRepository regionRepository) {
    int number = COUNTER.getAndIncrement();
    Region region = new Region();
    region.setCode(Integer.toString(number % (36 * 36) + 36 * 36, 36).substring(1));
    region.setName("Test Region " + number);
    return regionRepository.save(region);
  }
}
