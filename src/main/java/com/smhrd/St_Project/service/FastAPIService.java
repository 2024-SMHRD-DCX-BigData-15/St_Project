package com.smhrd.St_Project.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FastAPIService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> getPredictionFromFlask() {
        Map<String, Object> body = new HashMap<>();
        body.put("input", "dummy");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 응답 받기
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8000/predict",
                entity,
                Map.class
        );

        // 받아온 prediction 데이터 꺼내기
        List<List<Double>> prediction = (List<List<Double>>) response.getBody().get("prediction");

        // 안에 있는 첫 번째 결과만 꺼내서 리턴
        return prediction.get(0);
    }
}