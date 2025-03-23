package com.smhrd.St_Project.controller;

import org.springframework.web.bind.annotation.*;
import com.smhrd.St_Project.service.FastAPIService;
import java.util.List;

@RestController
public class AlarmRestController {

	private final FastAPIService FastAPIService;

    public AlarmRestController(FastAPIService FastAPIService) {
        this.FastAPIService = FastAPIService;
    }

    @GetMapping("/test-predict")
    public List<Double> testPredict() {
        return FastAPIService.getPredictionFromFlask();
    }
}