package com.smhrd.St_Project.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smhrd.St_Project.service.MemberService;

import jakarta.servlet.http.HttpSession;

import com.smhrd.St_Project.entity.MemberEntity;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;

	// 회원가입 기능 (비밀번호 암호화 적용)
    @PostMapping("/register.do")
    public String register(@RequestParam String id, @RequestParam String pw, 
                           @RequestParam String name, @RequestParam String add, 
                           @RequestParam String phone, @RequestParam char status, 
                           @RequestParam char role, @RequestParam Timestamp joinedat) {

        MemberEntity member = new MemberEntity();
        member.setUserId(id);
        member.setUserPw(pw); // 암호화는 Service에서 처리
        member.setUserName(name);
        member.setUserAdd(add);
        member.setUserPhone(phone);
        member.setUserStatus(status);
        member.setUserRole(role);
        member.setJoinedAt(joinedat);

        memberService.register(member);

        return "login"; // 회원가입 후 로그인 페이지로 이동
    }

    @PostMapping("/login.do")
    public String login(@RequestParam(required = false) String id, 
                        @RequestParam(required = false) String pw, 
                        HttpSession session) {

        // id 또는 pw 값이 없는 경우 예외 처리
        if (id == null || id.isEmpty() || pw == null || pw.isEmpty()) {
            System.out.println("로그인 실패: ID 또는 비밀번호가 비어있음");
            return "redirect:/login?error=missing_credentials";
        }

        System.out.println("로그인 요청: ID=" + id + ", PW=" + pw);

        MemberEntity member = memberService.login(id, pw);

        if (member != null) {
            session.setAttribute("loginUser", member);
            System.out.println("로그인 성공: " + id);
            return "redirect:/maindashboard";
        } else {
            System.out.println("로그인 실패: 사용자 없음");
            return "redirect:/login?error=true";
        }
    }



	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loginUser");

		return "redirect:/";
	}

}
