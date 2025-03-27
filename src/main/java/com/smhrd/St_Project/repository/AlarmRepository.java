package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    // 특정 tankIdx에 해당하는 알람 목록 조회11
    List<AlarmEntity> findByTankTankIdx(Long tankIdx);
}