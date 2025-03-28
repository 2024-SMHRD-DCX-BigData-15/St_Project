package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.AlarmEntity;
import com.smhrd.St_Project.entity.TankDataEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.repository.AlarmRepository;
import com.smhrd.St_Project.repository.TankDataRepository;
import com.smhrd.St_Project.repository.TankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TankDataService {

    @Autowired
    private TankDataRepository tankDataRepository;

    @Autowired
    private TankRepository tankRepository;
    
    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private FastAPIService fastAPIService;

    private static final int TOTAL_TANKS = 4;

    // 급격한 변화량 임계값 정의
    private static final BigDecimal PH_CHANGE_THRESHOLD = new BigDecimal("0.5");
    private static final BigDecimal DO_CHANGE_THRESHOLD = new BigDecimal("1.0");
    private static final BigDecimal TEMP_CHANGE_THRESHOLD = new BigDecimal("1.0");
    private static final BigDecimal SALT_CHANGE_THRESHOLD = new BigDecimal("2.0");
    private static final BigDecimal AMMONIA_CHANGE_THRESHOLD = new BigDecimal("0.05");
    private static final BigDecimal NITRITE_CHANGE_THRESHOLD = new BigDecimal("0.5");

    // 각 항목의 실제 최소/최대값 정의 (정규화 역변환용) - 이미지 기준으로 업데이트
    private static final BigDecimal PH_MIN = new BigDecimal("7.3");
    private static final BigDecimal PH_MAX = new BigDecimal("9.2");
    private static final BigDecimal DO_MIN = new BigDecimal("3.502");
    private static final BigDecimal DO_MAX = new BigDecimal("15.0");
    private static final BigDecimal TEMP_MIN = new BigDecimal("24.77");
    private static final BigDecimal TEMP_MAX = new BigDecimal("31.0");
    private static final BigDecimal SALT_MIN = new BigDecimal("15.0");
    private static final BigDecimal SALT_MAX = new BigDecimal("45.0");
    private static final BigDecimal AMMONIA_MIN = new BigDecimal("0.0");
    private static final BigDecimal AMMONIA_MAX = new BigDecimal("0.21");
    private static final BigDecimal NITRITE_MIN = new BigDecimal("0.0001");
    private static final BigDecimal NITRITE_MAX = new BigDecimal("6.0");

    @Transactional
    public void saveTankData(List<String[]> csvData) {
        int rowIndex = 0;

        for (String[] nextRecord : csvData) {
            try {
                if (nextRecord.length < 6) {
                    System.out.println("❌ 잘못된 데이터 형식! 건너뜀: " + String.join(", ", nextRecord));
                    continue;
                }

                Long tankIdx = (long) ((rowIndex % TOTAL_TANKS) + 1);
                rowIndex++;

                TankEntity tankEntity = tankRepository.findById(tankIdx).orElse(null);
                if (tankEntity == null) {
                    System.out.println("❌ 수조 정보 없음! tankIdx=" + tankIdx + " 건너뜀");
                    continue;
                }

                TankDataEntity tankData = new TankDataEntity();
                tankData.setTank(tankEntity);
                tankData.setRecordDate(new Timestamp(System.currentTimeMillis()));

                tankData.setWaterPh(convertToBigDecimal(nextRecord[0], "water_ph"));
                tankData.setWaterDo(convertToBigDecimal(nextRecord[1], "water_do"));
                tankData.setWaterTemp(convertToBigDecimal(nextRecord[2], "water_temp"));
                tankData.setWaterSalt(convertToBigDecimal(nextRecord[3], "water_salt"));
                tankData.setWaterAmmonia(convertToBigDecimal(nextRecord[4], "water_ammonia"));
                tankData.setWaterNitrogen(convertToBigDecimal(nextRecord[5], "water_nitrogen"));

                tankDataRepository.save(tankData);
                System.out.println("✅ 데이터 저장 완료! tankIdx=" + tankIdx + ", 저장 시간=" + tankData.getRecordDate());

                alarmService.checkAndCreateAlarm(tankData);

                // FastAPIService에서 단일 예측값 가져오기
                List<Double> prediction = fastAPIService.getSinglePrediction(tankIdx);
                if (prediction.size() == 6) {
                    checkAndCreatePredictionAlarm(tankData, prediction);
                }

                if (rowIndex % TOTAL_TANKS == 0) {
                    System.out.println("✅ 4개 데이터 저장 완료! (현재 rowIndex=" + rowIndex + ")");
                }

            } catch (Exception e) {
                System.out.println("❌ 데이터 변환 오류! 건너뜀: " + String.join(", ", nextRecord));
                e.printStackTrace();
            }
        }
    }

    private void checkAndCreatePredictionAlarm(TankDataEntity tankData, List<Double> prediction) {
        TankEntity tank = tankData.getTank();
        Timestamp recordDate = tankData.getRecordDate();

        BigDecimal currentPh = tankData.getWaterPh();
        BigDecimal currentDo = tankData.getWaterDo();
        BigDecimal currentTemp = tankData.getWaterTemp();
        BigDecimal currentSalt = tankData.getWaterSalt();
        BigDecimal currentAmmonia = tankData.getWaterAmmonia();
        BigDecimal currentNitrogen = tankData.getWaterNitrogen();

        // 예측값을 역변환 (0~1 → 실제 스케일)
        BigDecimal predictedPh = denormalize(new BigDecimal(prediction.get(0).toString()), PH_MIN, PH_MAX);
        BigDecimal predictedDo = denormalize(new BigDecimal(prediction.get(1).toString()), DO_MIN, DO_MAX);
        BigDecimal predictedTemp = denormalize(new BigDecimal(prediction.get(2).toString()), TEMP_MIN, TEMP_MAX);
        BigDecimal predictedSalt = denormalize(new BigDecimal(prediction.get(3).toString()), SALT_MIN, SALT_MAX);
        BigDecimal predictedAmmonia = denormalize(new BigDecimal(prediction.get(4).toString()), AMMONIA_MIN, AMMONIA_MAX);
        BigDecimal predictedNitrogen = denormalize(new BigDecimal(prediction.get(5).toString()), NITRITE_MIN, NITRITE_MAX);

        // 소수점 2자리로 반올림
        predictedPh = predictedPh.setScale(2, BigDecimal.ROUND_HALF_UP);
        predictedDo = predictedDo.setScale(2, BigDecimal.ROUND_HALF_UP);
        predictedTemp = predictedTemp.setScale(2, BigDecimal.ROUND_HALF_UP);
        predictedSalt = predictedSalt.setScale(2, BigDecimal.ROUND_HALF_UP);
        predictedAmmonia = predictedAmmonia.setScale(2, BigDecimal.ROUND_HALF_UP);
        predictedNitrogen = predictedNitrogen.setScale(2, BigDecimal.ROUND_HALF_UP);

        checkAndSavePredictionAlarm(tank, currentPh, predictedPh, PH_CHANGE_THRESHOLD, "산성도", recordDate);
        checkAndSavePredictionAlarm(tank, currentDo, predictedDo, DO_CHANGE_THRESHOLD, "용존산소", recordDate);
        checkAndSavePredictionAlarm(tank, currentTemp, predictedTemp, TEMP_CHANGE_THRESHOLD, "수온", recordDate);
        checkAndSavePredictionAlarm(tank, currentSalt, predictedSalt, SALT_CHANGE_THRESHOLD, "염도", recordDate);
        checkAndSavePredictionAlarm(tank, currentAmmonia, predictedAmmonia, AMMONIA_CHANGE_THRESHOLD, "암모니아", recordDate);
        checkAndSavePredictionAlarm(tank, currentNitrogen, predictedNitrogen, NITRITE_CHANGE_THRESHOLD, "아질산", recordDate);
    }

    // 정규화된 값을 실제 스케일로 역변환
    private BigDecimal denormalize(BigDecimal normalizedValue, BigDecimal min, BigDecimal max) {
        // normalizedValue * (max - min) + min
        BigDecimal range = max.subtract(min);
        BigDecimal scaled = normalizedValue.multiply(range);
        BigDecimal denormalized = scaled.add(min);
        return denormalized;
    }

    private void checkAndSavePredictionAlarm(TankEntity tank, BigDecimal currentValue, BigDecimal predictedValue, BigDecimal threshold, String dataName, Timestamp recordDate) {
        if (currentValue == null || predictedValue == null) {
            System.out.println("⚠️ " + dataName + " 값이 null입니다. 예측 알람 생성 스킵.");
            return;
        }

        BigDecimal change = predictedValue.subtract(currentValue).abs();
        if (change.compareTo(threshold) >= 0) {
            String alarmMsg = String.format("%s %s → %s (⚠️ 경고! 급격한 변화량 예상)", dataName, currentValue.toString(), predictedValue.toString());

            AlarmEntity alarm = new AlarmEntity();
            alarm.setTank(tank);
            alarm.setAlarmCreatedAt(recordDate);
            alarm.setAlarmMsg(alarmMsg);
            alarm.setAlarmRead('N');
            alarm.setAlarmReadAt(null);

            alarmRepository.save(alarm);
            System.out.println("✅ 예측 알람 생성: tankIdx=" + tank.getTankIdx() + ", 메시지=" + alarmMsg);
        } else {
            System.out.println("✅ " + dataName + " 변화량 정상: " + change + " (임계값: " + threshold + ")");
        }
    }

    private BigDecimal convertToBigDecimal(String value, String columnName) {
        try {
            BigDecimal convertedValue = new BigDecimal(value).setScale(6, BigDecimal.ROUND_HALF_UP);
            System.out.println("✅ " + columnName + " 변환 성공: " + value + " → " + convertedValue);
            return convertedValue;
        } catch (NumberFormatException e) {
            System.out.println("❌ " + columnName + " 변환 실패! 잘못된 숫자 형식: " + value);
            return BigDecimal.ZERO;
        }
    }

    public TankDataEntity getLatestTankData(Long tankIdx) {
        System.out.println("🚀 최신 수조 데이터 요청: tankIdx=" + tankIdx);
        Optional<TankDataEntity> latestData = tankDataRepository.findLatestTankData(tankIdx);
        if (latestData.isPresent()) {
            System.out.println("✅ 최신 데이터 반환 완료: " + latestData.get());
            return latestData.get();
        } else {
            System.out.println("❌ 최신 데이터 없음: tankIdx=" + tankIdx);
            return null;
        }
    }
}