package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.TankDataEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TankDataRepository extends JpaRepository<TankDataEntity, Long> {

    @Query("SELECT t FROM TankDataEntity t WHERE t.tank.tankIdx = :tankIdx ORDER BY t.recordNum DESC LIMIT 1")
    Optional<TankDataEntity> findLatestTankData(@Param("tankIdx") Long tankIdx);

    // 최근 18개 데이터 조회 메서드 추가
    @Query("SELECT t FROM TankDataEntity t WHERE t.tank.tankIdx = :tankIdx ORDER BY t.recordNum DESC LIMIT 18")
    List<TankDataEntity> findTop18ByTankIdxOrderByRecordNumDesc(@Param("tankIdx") Long tankIdx);
}