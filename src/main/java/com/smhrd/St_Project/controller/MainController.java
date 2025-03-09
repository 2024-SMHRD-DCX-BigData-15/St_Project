package com.smhrd.St_Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public String main() {
		return "main";
	}

	// 로그인 페이지 이동
	@GetMapping("/login")
	public String login() {
		return "login";
	}

}
