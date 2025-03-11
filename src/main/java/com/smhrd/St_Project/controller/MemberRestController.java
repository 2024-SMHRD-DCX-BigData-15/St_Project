package com.smhrd.St_Project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.util.PasswordEncryptor;
import com.smhrd.St_Project.entity.MemberEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    // 자동 로그인 API (SHA-256 암호화된 비밀번호 비교)
    @PostMapping("/auto-login")
    public String autoLogin(@RequestBody Map<String, String> request, HttpSession session) {
        String id = request.get("id");
        String rawPw = request.get("pw"); // ✅ 사용자가 입력한 원래 비밀번호 (평문)

        System.out.println("자동 로그인 요청: ID=" + id);

        // 🔹 DB에서 ID로 사용자 조회
        Optional<MemberEntity> optionalMember = memberService.findDetail(id);

        if (optionalMember.isPresent()) {
            MemberEntity member = optionalMember.get();

            if (member.getUserPw() == null) {
                System.out.println("❌ 오류: 사용자의 비밀번호가 DB에 없음!");
                return "FAIL";
            }

            // ✅ 서버에서 한 번만 암호화 후 비교
            String hashedPw = PasswordEncryptor.encryptSHA256(rawPw);
            System.out.println("🔹 입력된 비밀번호 해시값 (서버에서 변환): " + hashedPw);
            System.out.println("🔹 DB 저장된 비밀번호 해시값: " + member.getUserPw());

            if (hashedPw.equals(member.getUserPw())) {
                session.setAttribute("loginUser", member); // ✅ 세션에 로그인 정보 저장
                System.out.println("✅ 자동 로그인 성공: " + id);
                return "SUCCESS";
            } else {
                System.out.println("❌ 비밀번호 불일치");
            }
        } else {
            System.out.println("❌ 자동 로그인 실패: ID 없음");
        }

        return "FAIL";
    }

}
