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

@Service
public class TankService {

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 🔹 수조 추가 기능
     */
    public TankEntity addTank(String userId, BigDecimal tankWidth, BigDecimal tankHeight,
                              String tankLocation, String fishType, LocalDate startedAt) {
        // ✅ 사용자 찾기 (존재 여부 확인)
        Optional<MemberEntity> userOpt = memberRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("❌ 오류: 사용자를 찾을 수 없습니다. userId=" + userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
        MemberEntity user = userOpt.get();
        System.out.println("✅ 사용자 확인 완료: " + user.getUserId());

        // ✅ 새로운 수조 객체 생성
        TankEntity tank = new TankEntity();
        tank.setUser(user);
        tank.setTankWidth(tankWidth);
        tank.setTankHeight(tankHeight);
        tank.setTankLocation(tankLocation);
        tank.setFishType(fishType);
        tank.setStartedAt(startedAt);

        System.out.println("📌 새로운 수조 생성: " + tank);

        // ✅ DB에 저장 (tankIdx 자동 생성됨)
        TankEntity savedTank = tankRepository.save(tank);
        System.out.println("✅ 수조 추가 완료! tankIdx=" + savedTank.getTankIdx());

        return savedTank;
    }

    /**
     * 🔹 특정 사용자의 수조 목록 조회 기능
     */
    public List<TankEntity> getUserTanks(String userId) {
        // ✅ 사용자 찾기 (존재 여부 확인)
        Optional<MemberEntity> userOpt = memberRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("❌ 오류: 사용자를 찾을 수 없습니다. userId=" + userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }

        MemberEntity user = userOpt.get();
        System.out.println("✅ 사용자 확인 완료: " + user.getUserId());

        // ✅ 해당 사용자의 수조 목록 가져오기
        List<TankEntity> tanks = tankRepository.findByUser(user);
        System.out.println("✅ 수조 목록 조회 완료! 총 개수: " + tanks.size());

        return tanks;
    }
}
