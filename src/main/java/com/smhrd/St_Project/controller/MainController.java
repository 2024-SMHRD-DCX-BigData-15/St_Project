package com.smhrd.St_Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.smhrd.St_Project.entity.MemberEntity;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@GetMapping("/")
	public String main() {
		return "login";
	}

	// 회원가입 페이지 이동
	@GetMapping("/register")
	public String login() {
		return "register";
	}
	
	// 메인대시보드 페이지 이동
	@GetMapping("/maindashboard")
	public String showMainDashboard(HttpSession session) {
	    MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");
	    if (loginUser != null) {
	        System.out.println("Login User: " + loginUser.getUserName());  // 로그인한 사용자의 이름 출력
	    } else {
	        System.out.println("No user is logged in.");
	    }
	    return "maindashboard";  // maindashboard.html로 반환
	}

	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}

		

}
