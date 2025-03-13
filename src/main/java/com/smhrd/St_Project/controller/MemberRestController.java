package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.MemberService;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    /**
     * 아이디 중복 체크 API11
     * @param userId 클라이언트가 입력한 아이디
     * @return true(중복 없음), false(중복 있음)
     */
    @GetMapping("/checkId")
    public boolean checkId(@RequestParam String userId) {
        boolean isAvailable = !memberService.isIdExist(userId);

        // ✅ 디버깅 로그
        System.out.println("아이디 중복 체크 요청: " + userId + " -> 사용 가능 여부: " + isAvailable);

        return isAvailable;
    }
    
    @PostMapping("/delete")
    public ResponseEntity<String> deleteMember(@RequestBody Map<String, String> requestData) {
        String userId = requestData.get("id");
        String password = requestData.get("pw");

        // 📌 콘솔 디버깅 로그
        System.out.println("[디버깅] 탈퇴 요청 아이디: " + userId);
        System.out.println("[디버깅] 입력된 비밀번호: " + password);

        boolean isDeleted = memberService.deleteMember(userId, password);

        if (isDeleted) {
            System.out.println("[디버깅] 회원 탈퇴 성공!");
            return ResponseEntity.ok("회원 탈퇴 성공");
        } else {
            System.out.println("[디버깅] 회원 탈퇴 실패: 아이디 또는 비밀번호 오류");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 탈퇴 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }
    
    /**
     * 🔹 로그인 API (자동 로그인 지원)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestData) {
        String userId = requestData.get("id");
        String password = requestData.get("pw");
        boolean autoLogin = Boolean.parseBoolean(requestData.get("autoLogin"));

        // 🔹 비밀번호 암호화 후 로그인 검증
        String encryptedPassword = memberService.encryptPassword(password);
        MemberEntity member = memberService.login(userId, encryptedPassword);

        Map<String, Object> response = new HashMap<>();
        if (member != null) {
            String token = memberService.generateAuthToken(member); // 🔹 인증 토큰 생성
            response.put("success", true);
            response.put("token", autoLogin ? token : null); // 자동 로그인 체크 시 토큰 반환
        } else {
            response.put("success", false);
            response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 자동 로그인 API (토큰 검증)
     */
    @PostMapping("/autoLogin")
    public ResponseEntity<?> autoLogin(@RequestBody Map<String, String> requestData, HttpSession session) {
        String token = requestData.get("token");
        System.out.println("📌 자동 로그인 요청 받음: " + token);

        // 🔹 토큰 검증
        MemberEntity member = memberService.validateAuthToken(token);
        if (member != null) {
            System.out.println("✅ 자동 로그인 성공: " + member.getUserId());

            // 🔹 자동 로그인 성공 시 세션에 로그인 정보 저장 (무한 루프 방지)
            session.setAttribute("loginUser", member);
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            System.out.println("🚨 자동 로그인 실패: 토큰 무효");
            return ResponseEntity.status(401).body(Map.of("success", false));
        }
    }


    /**
     * 🔹 로그아웃 API (토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> requestData) {
        String token = requestData.get("token");
        memberService.removeAuthToken(token);
        System.out.println("✅ 로그아웃 완료 - 토큰 삭제");
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
    
    // ✅ 계정 복구 API
    @PostMapping("/recover")
    public ResponseEntity<?> recoverAccount(@RequestBody Map<String, String> requestData) {
        String userId = requestData.get("id");
        String password = requestData.get("pw");

        boolean isRecovered = memberService.recoverMember(userId, password);
        if (isRecovered) {
            return ResponseEntity.ok(Map.of("message", "계정이 복구되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "아이디 또는 비밀번호 오류"));
        }
    }
}
