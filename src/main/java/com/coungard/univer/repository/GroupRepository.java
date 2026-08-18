package com.coungard.univer.repository;

import com.coungard.univer.entity.Group;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

  Page<Group> findBySemesterId(UUID semesterId, Pageable pageable);
}
