package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.TankDataEntity;
import com.smhrd.St_Project.entity.TankEntity;
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
    private AlarmService alarmService; // AlarmService 주입

    private static final int TOTAL_TANKS = 4; // ✅ 총 수조 개수 (4개)

    /**
     * 🔹 CSV 데이터 저장 (4개씩 저장하도록 수정)
     */
    @Transactional
    public void saveTankData(List<String[]> csvData) {
        int rowIndex = 0; // ✅ 현재 데이터 행 인덱스 (0부터 시작)

        // ✅ CsvDataScheduler에서 컬럼 헤더를 이미 건너뛰었으므로 추가적인 헤더 스킵 없음!

        // ✅ CSV 데이터 반복문 실행
        for (String[] nextRecord : csvData) {
            try {
                // ✅ CSV 데이터가 충분한지 검사
                if (nextRecord.length < 6) {
                    System.out.println("❌ 잘못된 데이터 형식! 건너뜀: " + String.join(", ", nextRecord));
                    continue;
                }

                // ✅ tank_idx를 1~4로 유지하도록 rowIndex를 4개 단위로 끊음
                Long tankIdx = (long) ((rowIndex % TOTAL_TANKS) + 1);
                rowIndex++; // ✅ rowIndex 증가

                // ✅ tank_idx에 해당하는 TankEntity 찾기
                TankEntity tankEntity = tankRepository.findById(tankIdx).orElse(null);

                if (tankEntity == null) {
                    System.out.println("❌ 수조 정보 없음! tankIdx=" + tankIdx + " 건너뜀");
                    continue;
                }

                TankDataEntity tankData = new TankDataEntity();
                tankData.setTank(tankEntity);

                // ✅ 현재 시간으로 record_date 설정
                tankData.setRecordDate(new Timestamp(System.currentTimeMillis()));

                // ✅ CSV 컬럼을 각각 매핑하여 저장
                tankData.setWaterPh(convertToBigDecimal(nextRecord[0], "water_ph"));
                tankData.setWaterDo(convertToBigDecimal(nextRecord[1], "water_do"));
                tankData.setWaterTemp(convertToBigDecimal(nextRecord[2], "water_temp"));
                tankData.setWaterSalt(convertToBigDecimal(nextRecord[3], "water_salt"));
                tankData.setWaterAmmonia(convertToBigDecimal(nextRecord[4], "water_ammonia"));
                tankData.setWaterNitrogen(convertToBigDecimal(nextRecord[5], "water_nitrogen"));

                // ✅ 데이터베이스에 저장
                tankDataRepository.save(tankData);
                System.out.println("✅ 데이터 저장 완료! tankIdx=" + tankIdx + ", 저장 시간=" + tankData.getRecordDate());

                // ✅ 알람 검증 및 생성
                alarmService.checkAndCreateAlarm(tankData);

                // ✅ 4개 저장할 때마다 로그 찍음
                if (rowIndex % TOTAL_TANKS == 0) {
                    System.out.println("✅ 4개 데이터 저장 완료! (현재 rowIndex=" + rowIndex + ")");
                }

            } catch (Exception e) {
                System.out.println("❌ 데이터 변환 오류! 건너뜀: " + String.join(", ", nextRecord));
                e.printStackTrace();
            }
        }
    }

    /**
     * 🔹 `e` 표기법을 포함한 소수를 `BigDecimal`로 변환하는 메서드
     */
    private BigDecimal convertToBigDecimal(String value, String columnName) {
        try {
            BigDecimal convertedValue = new BigDecimal(value).setScale(6, BigDecimal.ROUND_HALF_UP);
            System.out.println("✅ " + columnName + " 변환 성공: " + value + " → " + convertedValue);
            return convertedValue;
        } catch (NumberFormatException e) {
            System.out.println("❌ " + columnName + " 변환 실패! 잘못된 숫자 형식: " + value);
            return BigDecimal.ZERO;  // 오류 발생 시 0으로 설정
        }
    }
    
    /**
     * 🔹 tank_idx에 해당하는 최신 수조 데이터를 반환
     */
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