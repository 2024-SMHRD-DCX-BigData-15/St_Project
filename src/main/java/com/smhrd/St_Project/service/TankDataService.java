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

@Service
public class TankDataService {

    @Autowired
    private TankDataRepository tankDataRepository;

    @Autowired
    private TankRepository tankRepository;

    /**
     * 🔹 CSV 데이터 저장 (한 번에 여러 개 저장 가능)
     */
    @Transactional
    public void saveTankData(List<String[]> csvData) {
        for (String[] nextRecord : csvData) {
            try {
                // ✅ CSV 데이터가 충분한지 검사
                if (nextRecord.length < 6) {
                    System.out.println("❌ 잘못된 데이터 형식! 건너뜀: " + String.join(", ", nextRecord));
                    continue;
                }

                TankDataEntity tankData = new TankDataEntity();

                // ✅ tank_idx에 해당하는 TankEntity 찾기
                Long tankIdx = 1L; // 임시 값 (수조 ID를 직접 지정)
                TankEntity tankEntity = tankRepository.findById(tankIdx).orElse(null);

                if (tankEntity == null) {
                    System.out.println("❌ 수조 정보 없음! tankIdx=" + tankIdx + " 건너뜀");
                    continue;
                }
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
}
