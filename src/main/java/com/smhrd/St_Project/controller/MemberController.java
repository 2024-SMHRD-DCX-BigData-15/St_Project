package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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

        // 비밀번호가 null인지 확인
        if (password == null || password.isEmpty()) {
            System.out.println("🚨 비밀번호가 입력되지 않았습니다.");
            return "redirect:/register?error=emptyPassword";
        }

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

}
