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
    	    body.put("data", "Spring에서 보낸 요청");

    	    HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.APPLICATION_JSON);
    	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    	    ResponseEntity<Map> response = restTemplate.postForEntity(
    	            "http://localhost:8000/predict", entity, Map.class
    	    );

    	    // 👉 여기서 반환값이 리스트이므로 이렇게 처리!
    	    Object rawValue = response.getBody().get("prediction");

    	    if (rawValue instanceof List<?>) {
    	        List<?> rawList = (List<?>) rawValue;

    	        // 👉 숫자 리스트로 안전하게 변환
    	        List<Double> predictionList = new ArrayList<>();
    	        for (Object item : rawList) {
    	            if (item instanceof Number) {
    	                predictionList.add(((Number) item).doubleValue());
    	            }
    	        }

    	        System.out.println("✅ 예측 결과 리스트: " + predictionList);
    	        return predictionList;
    	    } else {
    	        throw new IllegalStateException("예측 결과 형식이 올바르지 않습니다: " + rawValue);
    	    }
    	}


 }