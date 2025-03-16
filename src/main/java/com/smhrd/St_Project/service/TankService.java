package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.repository.TankRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TankService {

    private static final Logger logger = LoggerFactory.getLogger(TankService.class); // ✅ Logger 선언

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 🔹 수조 추가 기능
     */
    public TankEntity addTank(String userId, BigDecimal tankWidth, BigDecimal tankHeight, 
                              String tankLocation, String fishType, LocalDate startedAt) {
        Optional<MemberEntity> userOpt = memberRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("❌ 사용자를 찾을 수 없습니다: " + userId);
        }
        MemberEntity user = userOpt.get();

        // ✅ 필수 필드 검증
        if (fishType == null || fishType.isEmpty()) {
            throw new IllegalArgumentException("❌ 품종(fish_type)은 필수 입력 값입니다.");
        }

        // ✅ 수조 엔터티 생성
        TankEntity tank = new TankEntity();
        tank.setUser(user);
        tank.setTankWidth(tankWidth != null ? tankWidth : BigDecimal.ZERO);
        tank.setTankHeight(tankHeight != null ? tankHeight : BigDecimal.ZERO);
        tank.setTankLocation(tankLocation != null ? tankLocation : "미지정");
        tank.setFishType(fishType);
        tank.setStartedAt(startedAt != null ? startedAt : LocalDate.now());

        logger.info("✅ 최종 저장될 수조 데이터: {}", tank); // ✅ Logger 객체로 변경

        return tankRepository.save(tank);
    }

    /**
     * 🔹 특정 사용자의 수조 목록 조회 기능
     */
    public List<TankEntity> getUserTanks(String userId) {
        // ✅ 사용자 찾기 (존재 여부 확인)
        Optional<MemberEntity> userOpt = memberRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.error("❌ 오류: 사용자를 찾을 수 없습니다. userId={}", userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }

        MemberEntity user = userOpt.get();
        logger.info("✅ 사용자 확인 완료: {}", user.getUserId());

        // ✅ 해당 사용자의 수조 목록 가져오기
        List<TankEntity> tanks = tankRepository.findByUser(user);
        logger.info("✅ 수조 목록 조회 완료! 총 개수: {}", tanks.size());

        return tanks;
    }

    /**
     * 🔹 특정 tankIdx에 해당하는 수조 정보 조회
     */
    public TankEntity getTankById(Long tankIdx) {
        System.out.println("🔍 TankService에서 수조 조회: tankIdx=" + tankIdx);

        Optional<TankEntity> optionalTank = tankRepository.findById(tankIdx);

        if (optionalTank.isPresent()) {
            System.out.println("✅ 수조 정보 찾음: " + optionalTank.get().toString());
            return optionalTank.get();
        } else {
            System.out.println("❌ 해당 tankIdx의 수조 없음: " + tankIdx);
            return null;
        }
    }

    
	
    /**
     * 🔹 수조 정보 업데이트
     */
    public void updateTank(Long tankIdx, BigDecimal tankWidth, BigDecimal tankHeight, String tankLocation, String fishType, LocalDate startedAt) {
        if (tankIdx == null) {
            logger.error("❌ 수조 ID가 null입니다. 업데이트 불가능.");
            return;
        }

        Optional<TankEntity> existingTankOpt = tankRepository.findById(tankIdx);

        if (existingTankOpt.isPresent()) {
            TankEntity existingTank = existingTankOpt.get();
            existingTank.setTankWidth(tankWidth);
            existingTank.setTankHeight(tankHeight);
            existingTank.setTankLocation(tankLocation);
            existingTank.setFishType(fishType);
            existingTank.setStartedAt(startedAt);

            tankRepository.save(existingTank); // 업데이트 수행
            logger.info("✅ 수조 정보 수정 완료: {}", tankIdx);
        } else {
            logger.error("❌ 해당 수조 정보를 찾을 수 없습니다: {}", tankIdx);
        }
    }
    
    // 🔹 수조 삭제 (tank_delete = 'Y'로 변경)
    public boolean deleteTank(Long tankIdx) {
        System.out.println("🔍 삭제할 수조 찾는 중... tankIdx = " + tankIdx); // ✅ 디버깅 로그

        Optional<TankEntity> tankOptional = tankRepository.findById(tankIdx);
        
        if (tankOptional.isPresent()) {
            TankEntity tank = tankOptional.get();
            System.out.println("✅ 수조 찾음! 삭제 처리 진행 tankIdx = " + tankIdx); // ✅ 찾았을 때 로그
            
            tank.setTank_delete("Y"); // ✅ 삭제 상태로 변경
            tankRepository.save(tank);
            System.out.println("✅ 수조 삭제 완료! tankIdx = " + tankIdx); // ✅ 삭제 완료 로그
            return true;
        } else {
            System.out.println("❌ 수조 찾을 수 없음! tankIdx = " + tankIdx); // ❌ 오류 로그
            return false; 
        }
    }
}
