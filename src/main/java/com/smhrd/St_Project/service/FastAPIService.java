package com.smhrd.St_Project.service;

import com.smhrd.St_Project.repository.TankDataRepository;
import com.smhrd.St_Project.entity.TankDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FastAPIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TankDataRepository tankDataRepository;

    public List<List<Double>> getPredictionFromFlask(Long tankIdx) {
        List<TankDataEntity> recentData = tankDataRepository.findTop18ByTankIdxOrderByRecordNumDesc(tankIdx);
        if (recentData.size() < 18) {
            throw new IllegalStateException("tankIdx " + tankIdx + "에 대해 최소 18개의 데이터가 필요합니다. 현재: " + recentData.size());
        }

        List<List<Double>> dataList = new ArrayList<>();
        for (TankDataEntity data : recentData) {
            List<Double> row = Arrays.asList(
                data.getWaterPh() != null ? data.getWaterPh().doubleValue() : 0.0,
                data.getWaterDo() != null ? data.getWaterDo().doubleValue() : 0.0,
                data.getWaterTemp() != null ? data.getWaterTemp().doubleValue() : 0.0,
                data.getWaterSalt() != null ? data.getWaterSalt().doubleValue() : 0.0,
                data.getWaterAmmonia() != null ? data.getWaterAmmonia().doubleValue() : 0.0,
                data.getWaterNitrogen() != null ? data.getWaterNitrogen().doubleValue() : 0.0
            );
            dataList.add(row);
        }

        System.out.println("✅ 전송할 데이터 크기: " + dataList.size() + " x " + (dataList.isEmpty() ? 0 : dataList.get(0).size()));
        System.out.println("✅ 전송할 데이터: " + dataList);

        Map<String, Object> body = new HashMap<>();
        body.put("data", dataList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8000/predict", entity, Map.class
        );

        Object rawValue = response.getBody().get("prediction");
        List<List<Double>> predictionList = new ArrayList<>();

        if (rawValue instanceof List<?>) {
            List<?> rawList = (List<?>) rawValue;
            List<Double> doubleRow = new ArrayList<>();
            for (Object num : rawList) {
                if (num instanceof Number) {
                    doubleRow.add(((Number) num).doubleValue());
                } else {
                    throw new IllegalStateException("예측 값이 숫자가 아닙니다: " + num);
                }
            }
            predictionList.add(doubleRow); // 단일 리스트를 List<List<Double>>로 감쌈
        } else {
            throw new IllegalStateException("예측 결과 형식이 올바르지 않습니다: " + rawValue);
        }

        System.out.println("✅ 예측 결과 리스트: " + predictionList);
        return predictionList;
    }

    public List<Double> getSinglePrediction(Long tankIdx) {
        List<List<Double>> predictions = getPredictionFromFlask(tankIdx);
        if (predictions.isEmpty()) {
            throw new IllegalStateException("예측 결과가 비어 있습니다.");
        }
        return predictions.get(0); // 첫 번째 예측값 반환
    }
}