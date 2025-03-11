package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TankRepository extends JpaRepository<TankEntity, Long> {
    List<TankEntity> findByUser(MemberEntity user); // 특정 사용자의 수조 리스트 조회
}
//