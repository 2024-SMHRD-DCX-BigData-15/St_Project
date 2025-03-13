package com.smhrd.St_Project.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.entity.MemberEntity;

import jakarta.servlet.http.HttpSession;

import com.smhrd.St_Project.entity.MemberEntity;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;

	// 회원가입 기능 (비밀번호 암호화 적용)
	@PostMapping("/register.do")
	public String register(@RequestParam String id, @RequestParam String pw, @RequestParam String name,
			@RequestParam String add, @RequestParam String phone, @RequestParam char status, @RequestParam char role,
			@RequestParam Timestamp joinedat) {

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

	

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션에서 로그인 정보 제거
        session.invalidate();

        // 로그아웃 후 홈 페이지로 리다이렉트
        return "redirect:/";
    }
}
