package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.service.MemberService;

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
}
