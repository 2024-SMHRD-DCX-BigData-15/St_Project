package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tank") // ✅ /tank 경로를 기본 설정
public class TankRestController {

    private static final Logger logger = LoggerFactory.getLogger(TankRestController.class);

    @Autowired
    private TankService tankService;

    /**
     * 🔹 세션을 활용한 특정 사용자의 수조 목록 조회 API (GET /tank/list)
     */
    @GetMapping("/list")
    public List<TankEntity> getUserTanks(HttpSession session) {
        // 11✅ 세션에서 로그인된 사용자 정보 가져오기
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            logger.error("❌ 로그인된 사용자 정보가 없습니다.");
            throw new IllegalStateException("❌ 로그인된 사용자 정보가 없습니다.");
        }

        logger.info("✅ {} 사용자의 수조 목록 조회 요청", loginUser.getUserId());
        return tankService.getUserTanks(loginUser.getUserId());
    }

    /**
     * 🔹 수조 추가 API (POST /tank/add)
     */
    @PostMapping("/add")
    public TankEntity addTank(@RequestBody TankEntity tank, HttpSession session) {
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            logger.error("❌ 로그인된 사용자 정보가 없습니다.");
            throw new IllegalStateException("❌ 로그인된 사용자 정보가 없습니다.");
        }

        logger.info("✅ {} 사용자의 수조 추가 요청", loginUser.getUserId());
        logger.debug("📌 받은 데이터: {}", tank);

        // ✅ 필수 필드 검증
        if (tank.getFishType() == null || tank.getFishType().isEmpty()) {
            throw new IllegalArgumentException("❌ 품종(fish_type)은 필수 입력 값입니다.");
        }

        return tankService.addTank(
            loginUser.getUserId(),
            tank.getTankWidth(),
            tank.getTankHeight(),
            tank.getTankLocation(),
            tank.getFishType(),
            tank.getStartedAt()
        );
    }
    // 🔹 특정 수조를 "삭제 상태(Y)"로 변경하는 API
    @PutMapping("/delete/{tankIdx}")
    public ResponseEntity<String> deleteTank(@PathVariable Long tankIdx) {
        System.out.println("🚀 DELETE 요청 수신: tankIdx = " + tankIdx); // ✅ 디버깅 로그

        boolean deleted = tankService.deleteTank(tankIdx);

        if (deleted) {
            System.out.println("✅ 수조 삭제 성공! tankIdx = " + tankIdx); // ✅ 삭제 성공 로그
            return ResponseEntity.ok("✅ 수조 삭제 성공");
        } else {
            System.out.println("❌ 수조 삭제 실패! tankIdx = " + tankIdx + " (해당 ID 없음)"); // ❌ 오류 로그
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 해당 수조를 찾을 수 없음");
        }
    }

}