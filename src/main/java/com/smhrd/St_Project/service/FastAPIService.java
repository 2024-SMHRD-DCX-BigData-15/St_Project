package com.smhrd.St_Project.service;
 
 import org.springframework.http.*;
 import org.springframework.stereotype.Service;
 import org.springframework.web.client.RestTemplate;
 
 import java.util.*;
 
 @Service
 public class FastAPIService {
 
     private final RestTemplate restTemplate = new RestTemplate();
 
     public List<List<Double>> getPredictionFromFlask() {
    	    Map<String, Object> body = new HashMap<>();
    	    body.put("data", "Spring에서 보낸 요청");

    	    HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.APPLICATION_JSON);
    	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    	    ResponseEntity<Map> response = restTemplate.postForEntity(
    	            "http://localhost:8000/predict", entity, Map.class
    	    );

    	    Object rawValue = response.getBody().get("prediction");

    	    if (rawValue instanceof List<?>) {
    	        List<?> rawList = (List<?>) rawValue;
    	        List<List<Double>> predictionList = new ArrayList<>();

    	        for (Object row : rawList) {
    	            if (row instanceof List<?>) {
    	                List<?> subList = (List<?>) row;
    	                List<Double> doubleRow = new ArrayList<>();
    	                for (Object num : subList) {
    	                    if (num instanceof Number) {
    	                        doubleRow.add(((Number) num).doubleValue());
    	                    }
    	                }
    	                predictionList.add(doubleRow);
    	            }
    	        }

    	        System.out.println("✅ 예측 결과 리스트: " + predictionList);
    	        return predictionList;
    	    } else {
    	        throw new IllegalStateException("예측 결과 형식이 올바르지 않습니다: " + rawValue);
    	    }
    	}

    	}


