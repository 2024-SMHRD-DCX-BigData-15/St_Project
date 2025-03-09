package com.smhrd.St_Project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smhrd.St_Project.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // API 엔드포인트 네임스페이스 지정
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // 아이디 중복 체크
    @PostMapping("/check-id")
    public Map<String, Boolean> checkUserId(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // JSON 데이터에서 userId 가져오기
        System.out.println("아이디 중복 체크 요청: " + userId); // 콘솔 로그 추가

        boolean isDuplicated = memberService.isUserIdExists(userId); // 서비스 메서드 호출
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicated", isDuplicated);
        return response;
    }
}
