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
	
		// 회원가입 기능 구현
		// @RequestParam("id") boot는 ()안 생략 가능!! 단, 변수명이 html의 id명과 일치
		@PostMapping("/register.do")
		public String register(@RequestParam String id, @RequestParam String pw, 
				@RequestParam String name, @RequestParam String add, @RequestParam String phone,
				@RequestParam char status, @RequestParam char role, @RequestParam Timestamp joinedat) {
			
			MemberEntity member = new MemberEntity();
			member.setUserId(id);
			member.setUserPw(pw);
			member.setUserName(name);
			member.setUserAdd(add);
			member.setUserPhone(phone);
			member.setUserStatus(status);
			member.setUserRole(role);
			member.setJoinedAt(joinedat);
			
			memberService.register(member);
			
			// 회원가입이 완료가 되면 login.html이동
			return "login";
		}
		
		
		
		// 로그인 기능 구현
		@GetMapping("/login.do")
		public String login(@RequestParam String id, @RequestParam String pw,
				HttpSession session) {
			
			MemberEntity member = memberService.login(id,pw);
			if(member != null) {
				session.setAttribute("loginUser", member);
				return "redirect:/";			
			}else {
				// 로그인 실패시 alert 창 띄우기
				return "redirect:/login?error=true";
			}
		}
		
		@GetMapping("/logout")
		public String logout(HttpSession session) {
			session.removeAttribute("loginUser");
			
			return "redirect:/";
		}

}
