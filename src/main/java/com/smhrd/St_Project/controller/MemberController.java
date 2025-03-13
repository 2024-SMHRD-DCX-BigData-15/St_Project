package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 회원가입 페이지 이동
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 회원가입 처리
    @PostMapping("/register.do")
    public String register(@RequestParam("id") String userId,
                           @RequestParam("pw") String userPw,
                           @RequestParam("name") String userName,
                           @RequestParam(value = "add", required = false) String userAdd,
                           @RequestParam(value = "phone", required = false) String userPhone,
                           Model model) {
        
        System.out.println("회원가입 요청: " + userId);
        
        // 비밀번호 해싱 (SHA-256 사용)
        String hashedPw = hashPassword(userPw);
        
        // 엔티티 객체 생성
        MemberEntity member = new MemberEntity();
        member.setUserId(userId);
        member.setUserPw(hashedPw); // 암호화된 비밀번호 저장
        member.setUserName(userName);
        member.setUserAdd(userAdd);
        member.setUserPhone(userPhone);
        member.setUserStatus('N'); // 기본값 비활성화
        member.setUserRole('1'); // 일반 사용자
        member.setJoinedAt(Timestamp.valueOf(LocalDateTime.now())); // 현재 시간 설정
        member.setDeletedAt(null); // 탈퇴 시점은 null

        // 서비스 계층을 통해 저장
        boolean result = memberService.registerMember(member);
        
        if (result) {
            System.out.println("회원가입 성공: " + userId);
            return "redirect:/login";
        } else {
            System.out.println("회원가입 실패: " + userId);
            model.addAttribute("error", "회원가입에 실패했습니다.");
            return "register";
        }
    }

    // ID 중복 체크 API
    @GetMapping("/checkId")
    @ResponseBody
    public boolean checkId(@RequestParam("id") String userId) {
        System.out.println("ID 중복 체크 요청: " + userId);
        return memberService.isIdAvailable(userId);
    }

    // 비밀번호 해싱 메서드 (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해싱 실패", e);
        }
    }
}