package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.repository.TankRepository;
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

	
	public void updateTank(TankEntity tank) {
	    TankEntity existingTank = tankRepository.findById(tank.getTankIdx())
	            .orElseThrow(() -> new IllegalArgumentException("해당 수조가 없습니다. ID: " + tank.getTankIdx()));

	    // 기존 데이터 수정
	    existingTank.setTankWidth(tank.getTankWidth());
	    existingTank.setTankHeight(tank.getTankHeight());
	    existingTank.setTankLocation(tank.getTankLocation());
	    existingTank.setFishType(tank.getFishType());
	    existingTank.setStartedAt(tank.getStartedAt());

	    tankRepository.save(existingTank);
	}

	public static TankEntity getTankById(Long tankIdx) {
		// TODO Auto-generated method stub
		return null;
	}

}
