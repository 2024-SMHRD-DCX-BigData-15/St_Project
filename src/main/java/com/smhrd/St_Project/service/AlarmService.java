package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.AlarmEntity;
import com.smhrd.St_Project.entity.TankDataEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.repository.AlarmRepository;
import com.smhrd.St_Project.repository.TankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class AlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmService.class);

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private TankRepository tankRepository;

    /**
     * 🔹 특정 tankIdx의 알람 목록 조회
     */
    public List<AlarmEntity> getAlarmsByTankIdx(Long tankIdx) {
        logger.info("✅ tankIdx={}의 알람 목록 조회 요청", tankIdx);
        List<AlarmEntity> alarms = alarmRepository.findByTankTankIdx(tankIdx);
        logger.info("✅ 조회된 알람 개수: {}", alarms.size());
        return alarms;
    }

    /**
     * 🔹 알람 확인 처리
     */
    @Transactional
    public void confirmAlarm(Long alarmNum) {
        logger.info("✅ 알람 확인 요청: alarmNum={}", alarmNum);
        AlarmEntity alarm = alarmRepository.findById(alarmNum)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 알람을 찾을 수 없습니다: " + alarmNum));
        alarm.setAlarmRead('Y');
        alarm.setAlarmReadAt(new Timestamp(System.currentTimeMillis()));
        alarmRepository.save(alarm);
        logger.info("✅ 알람 확인 완료: alarmNum={}", alarmNum);
    }

    /**
     * 🔹 모든 수조의 알람 목록 조회
     * - 모든 알람 데이터를 반환합니다.
     * - alarmHistory2.html에서 확인된 알람(alarmRead='Y')을 표시하기 위해 사용됩니다.
     * @return 모든 알람 데이터 리스트
     */
    public List<AlarmEntity> getAllAlarms() {
        logger.info("✅ 모든 수조의 알람 목록 조회 요청");
        List<AlarmEntity> alarms = alarmRepository.findAll();
        logger.info("✅ 조회된 모든 알람 개수: {}", alarms.size());
        // 디버깅: 확인된 알람(alarmRead='Y') 개수 확인
        long confirmedAlarmsCount = alarms.stream()
                .filter(alarm -> alarm.getAlarmRead() == 'Y')
                .count();
        logger.info("📊 확인된 알람(alarmRead='Y') 개수: {}", confirmedAlarmsCount);
        return alarms;
    }

    /**
     * 🔹 수질 데이터 검증 및 알람 생성
     */
    @Transactional
    public void checkAndCreateAlarm(TankDataEntity tankData) {
        logger.info("🚀 수질 데이터 검증 및 알람 생성 시작: tankIdx={}", tankData.getTank().getTankIdx());

        TankEntity tank = tankData.getTank();
        if (tank == null) {
            logger.error("❌ TankEntity가 null입니다. 알람 생성 중단.");
            return;
        }

        // 정상 범위 정의 (사진 기준)
        final BigDecimal PH_MIN = new BigDecimal("7.5");
        final BigDecimal PH_MAX = new BigDecimal("9.0");
        final BigDecimal DO_MIN = new BigDecimal("5.0");
        final BigDecimal DO_MAX = new BigDecimal("10.0");
        final BigDecimal TEMP_MIN = new BigDecimal("26.0");
        final BigDecimal TEMP_MAX = new BigDecimal("30.0");
        final BigDecimal SALT_MIN = new BigDecimal("0.5");
        final BigDecimal SALT_MAX = new BigDecimal("45.0");
        final BigDecimal AMMONIA_MIN = new BigDecimal("0.0");
        final BigDecimal AMMONIA_MAX = new BigDecimal("0.16");
        final BigDecimal NITRITE_MIN = new BigDecimal("0.0");
        final BigDecimal NITRITE_MAX = new BigDecimal("5.0");

        // 각 수질 항목 검증
        checkAndSaveAlarm(tank, tankData.getWaterPh(), PH_MIN, PH_MAX, "산성도", tankData.getRecordDate());
        checkAndSaveAlarm(tank, tankData.getWaterDo(), DO_MIN, DO_MAX, "용존산소", tankData.getRecordDate());
        checkAndSaveAlarm(tank, tankData.getWaterTemp(), TEMP_MIN, TEMP_MAX, "수온", tankData.getRecordDate());
        checkAndSaveAlarm(tank, tankData.getWaterSalt(), SALT_MIN, SALT_MAX, "염도", tankData.getRecordDate());
        checkAndSaveAlarm(tank, tankData.getWaterAmmonia(), AMMONIA_MIN, AMMONIA_MAX, "암모니아", tankData.getRecordDate());
        checkAndSaveAlarm(tank, tankData.getWaterNitrogen(), NITRITE_MIN, NITRITE_MAX, "아질산", tankData.getRecordDate());
    }

    /**
     * 🔹 개별 수질 항목 검증 및 알람 저장
     * - 정상 범위를 벗어나면 무조건 "위험"으로 저장
     * @param tank TankEntity 객체
     * @param value 수질 값
     * @param min 정상 범위 최소값
     * @param max 정상 범위 최대값
     * @param dataName 수질 항목 이름
     * @param recordDate 데이터 기록 시간
     */
    private void checkAndSaveAlarm(TankEntity tank, BigDecimal value, BigDecimal min, BigDecimal max, String dataName, Timestamp recordDate) {
        if (value == null) {
            logger.warn("⚠️ {} 값이 null입니다. 알람 생성 스킵.", dataName);
            return;
        }

        // 정상 범위 확인
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            // 정상 범위를 벗어나면 무조건 "위험"으로 설정
            String status = "🚨 위험";
            String alarmMsg = String.format("%s %s (%s 범위 벗어남)", dataName, value.toString(), status);

            AlarmEntity alarm = new AlarmEntity();
            alarm.setTank(tank);
            alarm.setAlarmCreatedAt(recordDate);
            alarm.setAlarmMsg(alarmMsg);
            alarm.setAlarmRead('N');
            alarm.setAlarmReadAt(null);

            alarmRepository.save(alarm);
            logger.info("✅ 알람 생성: tankIdx={}, 메시지={}", tank.getTankIdx(), alarmMsg);
        } else {
            logger.debug("✅ {} 값 정상: {} (범위: {} ~ {})", dataName, value, min, max);
        }
    }
}