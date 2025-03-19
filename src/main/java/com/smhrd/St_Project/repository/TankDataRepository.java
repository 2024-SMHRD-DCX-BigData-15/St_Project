package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.TankDataEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TankDataRepository extends JpaRepository<TankDataEntity, Long> {

    /**
     * ✅ 특정 수조의 최신 데이터 가져오기 (record_date 기준 최신 데이터)
     *    - tank_idx에 해당하는 가장 최신 데이터 가져오기
     */
	@Query("SELECT t FROM TankDataEntity t WHERE t.tank.tankIdx = :tankIdx ORDER BY t.recordNum DESC LIMIT 1")
	Optional<TankDataEntity> findLatestTankData(@Param("tankIdx") Long tankIdx);

}
