package com.coungard.univer.repository;

import com.coungard.univer.entity.Region;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {

  List<Region> findAllByOrderByNameAsc();
}
