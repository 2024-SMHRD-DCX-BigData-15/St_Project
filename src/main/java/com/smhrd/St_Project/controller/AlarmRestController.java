package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.AlarmEntity;
import com.smhrd.St_Project.service.AlarmService;
import com.smhrd.St_Project.service.FastAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alarms")
public class AlarmRestController {

    private static final Logger logger = LoggerFactory.getLogger(AlarmRestController.class);

    private final FastAPIService fastAPIService;
    private final AlarmService alarmService;

    @Autowired
    public AlarmRestController(FastAPIService fastAPIService, AlarmService alarmService) {
        this.fastAPIService = fastAPIService;
        this.alarmService = alarmService;
    }

    @GetMapping("/test-predict")
    public List<List<Double>> testPredict() {
        return fastAPIService.getPredictionFromFlask();
    }

    /**
     * 🔹 특정 tankIdx의 알람 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<List<AlarmEntity>> getAlarms(@RequestParam("tankIdx") Long tankIdx) {
        logger.info("🚀 알람 목록 조회 요청: tankIdx={}", tankIdx);
        List<AlarmEntity> alarms = alarmService.getAlarmsByTankIdx(tankIdx);
        logger.info("✅ 알람 목록 반환: 개수={}", alarms.size());
        return ResponseEntity.ok(alarms);
    }

    /**
     * 🔹 알람 확인 처리 API
     */
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmAlarm(@RequestBody Map<String, Long> request) {
        Long alarmNum = request.get("alarmNum");
        logger.info("🚀 알람 확인 요청: alarmNum={}", alarmNum);
        try {
            alarmService.confirmAlarm(alarmNum);
            logger.info("✅ 알람 확인 성공: alarmNum={}", alarmNum);
            return ResponseEntity.ok("✅ 알람 확인 성공");
        } catch (Exception e) {
            logger.error("❌ 알람 확인 실패: alarmNum={}, 오류={}", alarmNum, e.getMessage());
            return ResponseEntity.status(500).body("❌ 알람 확인 실패");
        }
    }

    /**
     * 🔹 모든 수조의 알람 목록 조회 API (alarmHistory2.html에서 사용)
     * - 모든 수조의 알람 데이터를 반환합니다.
     * - alarmHistory2.html에서 확인된 알람(alarmRead='Y')을 표시하기 위해 사용됩니다.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AlarmEntity>> getAllAlarms() {
        logger.info("🚀 모든 수조의 알람 목록 조회 요청");
        try {
            List<AlarmEntity> alarms = alarmService.getAllAlarms();
            logger.info("✅ 모든 알람 목록 반환: 개수={}", alarms.size());
            // 디버깅: 반환된 알람 데이터 일부 출력
            if (!alarms.isEmpty()) {
                logger.debug("📋 첫 번째 알람 데이터: alarmNum={}, tankIdx={}, alarmMsg={}, alarmRead={}",
                        alarms.get(0).getAlarmNum(),
                        alarms.get(0).getTank() != null ? alarms.get(0).getTank().getTankIdx() : "null",
                        alarms.get(0).getAlarmMsg(),
                        alarms.get(0).getAlarmRead());
            }
            return ResponseEntity.ok(alarms);
        } catch (Exception e) {
            logger.error("❌ 모든 알람 목록 조회 실패: 오류={}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}