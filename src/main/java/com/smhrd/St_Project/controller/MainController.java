package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@Autowired
    private TankService tankService;
	
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
    public String mainDashboard(HttpSession session) {
        // ✅ 세션에서 로그인 정보 확인
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            System.out.println("🚨 접근 오류: 로그인 필요 (세션에 loginUser 없음)");
            return "redirect:/login";
        }

        // ✅ 관리자 계정 확인 후 관리자 페이지로 이동
        if ("admin".equals(loginUser.getUserId())) {
            System.out.println("✅ 관리자 계정 로그인 감지! 관리자 페이지로 이동");
            return "redirect:/admin";
        }

        System.out.println("✅ 대시보드 접근 허용: " + loginUser.getUserId());
        return "maindashboard";
    }

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session) {
        // ✅ 세션에서 로그인 정보 확인
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        // ✅ 관리자가 아니라면 접근 차단
        if (loginUser == null || !"admin".equals(loginUser.getUserId())) {
            System.out.println("🚨 접근 오류: 관리자 계정이 아님! 로그인 페이지로 이동");
            return "redirect:/login";
        }

        System.out.println("✅ 관리자 페이지 접근 허용: " + loginUser.getUserId());
        return "admin";
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
    
    @GetMapping("/relogin")
    public String relogin() {
        return "relogin"; // relogin.html을 반환
    }

    @GetMapping("/tank")
    public String tank() {
        return "tank"; // tank.html 템플릿을 반환 (thymeleaf 사용 시 resources/templates/login.html 필요)
    }
    
    @GetMapping("/dashboarddetail")
    public String dashboarddetail(@RequestParam(value = "tankIdx", required = false) Long tankIdx, Model model) {
        if (tankIdx == null) {
            System.out.println("❌ tankIdx 값이 제공되지 않음");
            model.addAttribute("selectedTank", null); // tankIdx가 없을 경우
        } else {
            TankEntity tank = tankService.getTankById(tankIdx);
            if (tank != null) {
                System.out.println("✅ 수조 정보 로드 완료: " + tank.toString());
                model.addAttribute("selectedTank", tank);
            } else {
                System.out.println("❌ 해당 tankIdx에 대한 수조 정보 없음");
                model.addAttribute("selectedTank", null); // 해당 tankIdx가 없을 경우
            }
        }
        model.addAttribute("tankIdx", tankIdx);
        return "dashboarddetail";
    }
    
    @GetMapping("/alarmHistory")
    public String alarmHistory() {
        return "alarmHistory"; // alarmHistory.html 템플릿을 반환 (thymeleaf 사용 시 resources/templates/login.html 필요)
    }

    @GetMapping("/alarmHistory2")
    public String alarmHistory2() {
        return "alarmHistory2"; // alarmHistory2.html 템플릿을 반환 (thymeleaf 사용 시 resources/templates/login.html 필요)
    }
    
    @PostMapping("/admin")
    public String admin() {
        return "admin"; 
    }
}
