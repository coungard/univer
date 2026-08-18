package com.coungard.univer.repository;

import com.coungard.univer.entity.WeekScheduleCycle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekScheduleCycleRepository extends JpaRepository<WeekScheduleCycle, UUID> {

  Optional<WeekScheduleCycle> findBySemesterId(UUID semesterId);

  boolean existsBySemesterId(UUID semesterId);
}
