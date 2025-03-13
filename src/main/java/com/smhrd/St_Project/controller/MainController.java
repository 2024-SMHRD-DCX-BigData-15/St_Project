package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @GetMapping("/maindashboard")
    public String mainDashboard() {
        return "maindashboard"; // ✅ 로그인 성공 후 이동할 페이지
    }

    @GetMapping("/edit/{id}")
    public String editMember(@PathVariable("id") String userId, Model model, HttpSession session) {
        // 1. 로그인한 사용자 정보 가져오기
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        // 2. 로그인한 사용자가 없거나 다른 ID를 수정하려 하면 차단
        if (loginUser == null || !loginUser.getUserId().equals(userId)) {
            System.out.println("🚨 접근 오류: 로그인 필요 또는 권한 없음");
            return "redirect:/login";
        }

        // 3. 기존 회원 정보 가져와서 모델에 추가
        model.addAttribute("member", loginUser);
        return "edit"; // edit.html 반환
    }
    
    @GetMapping("/delete")
    public String deletePage() {
        return "delete"; // delete.html을 반환
    }


}
