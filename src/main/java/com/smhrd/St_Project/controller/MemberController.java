package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.MemberService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 회원 가입 처리
     * @param memberEntity 클라이언트가 입력한 회원 정보
     * @return 로그인 페이지로 리다이렉트
     */
    @PostMapping("/register.do")
    public String register(@RequestParam("id") String userId,
                           @RequestParam("pw") String password,
                           @RequestParam("name") String userName,
                           @RequestParam("add") String userAdd,
                           @RequestParam("phone") String userPhone) {

        // 비밀번호 암호화
        String encryptedPassword = memberService.encryptPassword(password);

        // 회원 정보 객체 생성
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUserId(userId);
        memberEntity.setUserPw(encryptedPassword);
        memberEntity.setUserName(userName);
        memberEntity.setUserAdd(userAdd);
        memberEntity.setUserPhone(userPhone);
        memberEntity.setUserStatus('N');
        memberEntity.setUserRole('1');
        memberEntity.setJoinedAt(Timestamp.valueOf(LocalDateTime.now()));
        // 회원 정보 저장
        memberService.registerMember(memberEntity);

        System.out.println("✅ 회원 가입 성공: " + userId);

        return "redirect:/login";
    }

    /**
     * 로그인 처리
     * @param userId 사용자 입력 ID
     * @param password 사용자 입력 비밀번호
     * @param session 로그인 유지 세션
     * @return 로그인 성공 시 대시보드, 실패 시 로그인 페이지로 이동
     */
    @PostMapping("/login.do")
    public String login(@RequestParam("id") String userId,
                        @RequestParam("pw") String password,
                        HttpSession session) {

        // 비밀번호 암호화 후 DB에서 검증
        String encryptedPassword = memberService.encryptPassword(password);
        MemberEntity member = memberService.login(userId, encryptedPassword);

        if (member != null) {
            // 로그인 성공 -> 세션 저장 후 대시보드로 이동
            session.setAttribute("loginUser", member);
            System.out.println("✅ 로그인 성공: " + userId);
            return "redirect:/maindashboard";
        } else {
            // 로그인 실패 -> 로그인 페이지로 이동 + 오류 메시지 전달
            System.out.println("🚨 로그인 실패 (ID 또는 비밀번호 불일치): " + userId);
            return "redirect:/login?error=invalid";
        }
    }
}
