package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@GetMapping("/")
    public String home() {
        return "login"; // login.html 템플릿을 반환 (thymeleaf 사용 시 resources/templates/login.html 필요)
    }
	
	 // "/register" URL 접속 시 회원가입 페이지로 이동
    @GetMapping("/register")
    public String register() {
        return "register"; // register.html 반환
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html 템플릿을 반환 (thymeleaf 사용 시 resources/templates/login.html 필요)
    }

}
