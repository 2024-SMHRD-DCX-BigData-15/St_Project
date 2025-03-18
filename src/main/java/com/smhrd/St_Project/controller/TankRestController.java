package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankDataEntity;
import com.smhrd.St_Project.service.TankDataService;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tank") // ✅ /tank 경로를 기본 설정
public class TankRestController {

    private static final Logger logger = LoggerFactory.getLogger(TankRestController.class);

    @Autowired
    private TankService tankService;
    @Autowired
    private TankDataService tankDataService;

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
    
    /**
     * 🔹 특정 tankIdx의 수조 정보를 JSON으로 반환
     */
    @GetMapping("/detail")
    public TankEntity getTankDetail(@RequestParam("tankIdx") Long tankIdx) {
        System.out.println("🚀 REST API: 수조 정보 요청 - tankIdx=" + tankIdx);
        Optional<TankEntity> tank = Optional.ofNullable(tankService.getTankById(tankIdx));
        return tank.orElse(null);
    }
    
    /**
     * 🔹 특정 수조의 최신 수질 데이터를 JSON으로 반환
     * ✅ 프론트엔드에서 데이터를 요청하면 해당 tankIdx의 최신 데이터가 반환됨
     */
    /**
     * 🔹 tank_idx에 해당하는 최신 수조 데이터를 JSON으로 반환
     */
    @GetMapping("/data/latest")
    public ResponseEntity<Map<String, Object>> getLatestTankData(@RequestParam("tankIdx") Long tankIdx) {
        TankDataEntity latestData = tankDataService.getLatestTankData(tankIdx);

        if (latestData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // ✅ JSON 변환 시 필요한 필드만 추출하여 반환
        Map<String, Object> response = new HashMap<>();
        response.put("waterPh", latestData.getWaterPh());
        response.put("waterDo", latestData.getWaterDo());
        response.put("waterTemp", latestData.getWaterTemp());
        response.put("waterSalt", latestData.getWaterSalt());
        response.put("waterAmmonia", latestData.getWaterAmmonia());
        response.put("waterNitrogen", latestData.getWaterNitrogen());
        response.put("recordDate", latestData.getRecordDate());

        return ResponseEntity.ok(response);
    }


}