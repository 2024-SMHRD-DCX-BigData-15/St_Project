package com.smhrd.St_Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}
