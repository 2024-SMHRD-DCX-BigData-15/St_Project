package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.TankDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TankDataRepository extends JpaRepository<TankDataEntity, Long> {
}
