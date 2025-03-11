package com.smhrd.St_Project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.entity.MemberEntity;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api") // API 엔드포인트 네임스페이스 지정
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // 아이디 중복 체크
    @PostMapping("/check-id")
    public Map<String, Boolean> checkUserId(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        System.out.println("아이디 중복 체크 요청: " + userId);

        boolean isDuplicated = memberService.isUserIdExists(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicated", isDuplicated);
        return response;
    }

 // 🔹 자동 로그인 API (SHA-256 암호화된 비밀번호 비교)
    @PostMapping("/auto-login")
    public String autoLogin(@RequestBody Map<String, String> request, HttpSession session) {
        String id = request.get("id");
        String encryptedPw = request.get("pw"); // ✅ 이미 암호화된 비밀번호 가져오기

        System.out.println("자동 로그인 요청: ID=" + id);

        // DB에서 ID와 암호화된 비밀번호로 사용자 조회
        MemberEntity member = memberService.login(id, encryptedPw);

        if (member != null) {
            session.setAttribute("loginUser", member);
            System.out.println("✅ 자동 로그인 성공: " + id);
            return "SUCCESS";
        } else {
            System.out.println("❌ 자동 로그인 실패");
            return "FAIL";
        }
    }


}
