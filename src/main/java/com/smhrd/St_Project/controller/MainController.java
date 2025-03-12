package com.smhrd.St_Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	// 계정복구 페이지 이동
	@GetMapping("/relogin")
	public String relogin() {
		return "relogin";
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
	
	// 대시보드상세 페이지 이동
		@GetMapping("/dashboarddetail")
		public String showDashboarddetail(HttpSession session) {
		  
		    return "dashboarddetail";  // maindashboard.html로 반환
		}

	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}

	
	@GetMapping("/tank")
	public String tank() {
		return "tank";
	}
	
	@GetMapping("/tankedit")
	public String tankedit() {
		return "tankedit";
	}
	
	@RequestMapping("/alarmHistory")
	public String alarmHistory() {
		return "alarmHistory";
	}
		

}
