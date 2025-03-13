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

    @Autowired
    private TankService tankService;

    /**
     * 🔹 기본 페이지 -> 로그인 페이지로 이동
     */
    @GetMapping("/")
    public String main() {
        return "login";
    }

    /**
     * 🔹 회원가입 페이지 이동
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 🔹 계정복구 페이지 이동
     */
    @GetMapping("/relogin")
    public String reloginPage() {
        return "relogin";
    }

    /**
     * 🔹 메인 대시보드 페이지 이동
     */
    @GetMapping("/maindashboard")
    public String showMainDashboard(HttpSession session) {
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        if (loginUser != null) {
            System.out.println("✅ 로그인된 사용자: " + loginUser.getUserName());
        } else {
            System.out.println("❌ 로그인된 사용자가 없습니다.");
        }

        return "maindashboard";  // maindashboard.html 반환
    }

    /**
     * 🔹 특정 수조 정보를 가져와서 `dashboarddetail.html`로 전달
     */
    @GetMapping("/dashboarddetail")
    public String showDashboardDetail(@RequestParam("tankIdx") Long tankIdx, Model model) {
        System.out.println("🚀 수조 상세 페이지 요청: tankIdx=" + tankIdx);

        // tankIdx가 null이면 메인 대시보드로 리디렉트
        if (tankIdx == null) {
            System.out.println("❌ tankIdx가 null입니다!");
            return "redirect:/maindashboard";
        }

        // 해당 tankIdx의 수조 정보 가져오기
        TankEntity tank = tankService.getTankById(tankIdx);

        if (tank == null) {
            System.out.println("❌ 해당 tankIdx의 수조 정보 없음: " + tankIdx);
            model.addAttribute("selectedTank", new TankEntity()); // 기본 빈 객체 추가
        } else {
            System.out.println("✅ 수조 정보 로드 완료: " + tank.toString());
            model.addAttribute("selectedTank", tank);
        }

        return "dashboarddetail";
    }

    /**
     * 🔹 로그인 페이지 이동
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 🔹 수조 등록 페이지 이동
     */
    @GetMapping("/tank")
    public String tankPage() {
        return "tank";
    }

    /**
     * 🔹 수조 수정 페이지 이동
     */
    @GetMapping("/tankedit")
    public String tankEditPage() {
        return "tankedit";
    }

    /**
     * 🔹 알람 히스토리 페이지 이동
     */
    @GetMapping("/alarmHistory")
    public String alarmHistoryPage() {
        return "alarmHistory";
    }

    @GetMapping("/alarmHistory2")
    public String alarmHistoryPage2() {
        return "alarmHistory2";
    }
}
