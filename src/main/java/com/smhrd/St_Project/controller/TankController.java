package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/tank")
public class TankController {

    private static final Logger logger = LoggerFactory.getLogger(TankController.class);

    @Autowired
    private TankService tankService;

    /**
     * 🔹 수조 수정 페이지 요청 (GET /tank/edit/{tankIdx})
     */
    @GetMapping("/edit/{tankIdx}")
    public String editTankPage(@PathVariable("tankIdx") Long tankIdx, Model model) {
        logger.info("🔍 수조 수정 페이지 요청: tankIdx={}", tankIdx);

        // 해당 tankIdx의 정보를 가져오기
        TankEntity tank = tankService.getTankById(tankIdx);
        if (tank == null) {
            logger.error("❌ 해당 수조 정보를 찾을 수 없습니다: {}", tankIdx);
            return "redirect:/maindashboard"; // 수조 정보를 찾을 수 없으면 대시보드로 리디렉트
        }

        model.addAttribute("tank", tank);
        return "tankedit"; // tankedit.html 반환
    }

    /**
     * 🔹 수조 정보 업데이트 (POST /tank/update)
     */
    @PostMapping("/update")
    public String updateTank(
            @RequestParam("tank_idx") Long tankIdx,
            @RequestParam("tank_width") BigDecimal tankWidth,
            @RequestParam("tank_height") BigDecimal tankHeight,
            @RequestParam("tank_location") String tankLocation,
            @RequestParam("fish_type") String fishType,
            @RequestParam("started_at") LocalDate startedAt,
            HttpSession session) {

        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            logger.error("❌ 로그인된 사용자 정보가 없습니다.");
            throw new IllegalStateException("❌ 로그인된 사용자 정보가 없습니다.");
        }

        logger.info("✅ {} 사용자의 수조 정보 수정 요청: {}", loginUser.getUserId(), tankIdx);

        if (tankIdx == null) {
            logger.error("❌ 수조 ID가 null입니다. 업데이트 불가능.");
            return "redirect:/maindashboard"; // 잘못된 요청 처리
        }

        tankService.updateTank(tankIdx, tankWidth, tankHeight, tankLocation, fishType, startedAt);

        return "redirect:/maindashboard"; // ✅ 수정 완료 후 대시보드로 이동
    }
    
    /**
     * 🔹 특정 수조 정보를 가져와서 `dashboarddetail.html`로 전달
     */
    @GetMapping("/dashboard/detail")
    public String getTankDetail(@RequestParam("tankIdx") Long tankIdx, Model model) {
        System.out.println("🚀 수조 정보 요청: tankIdx=" + tankIdx);

        if (tankIdx == null) {
            System.out.println("❌ tankIdx가 null입니다!");
            return "redirect:/maindashboard";
        }

        TankEntity tank = tankService.getTankById(tankIdx);

        if (tank == null) {
            System.out.println("❌ 해당 tankIdx의 수조 정보 없음: " + tankIdx);
            model.addAttribute("selectedTank", new TankEntity());
        } else {
            System.out.println("✅ 수조 정보 로드 완료: " + tank.toString());
            model.addAttribute("selectedTank", tank);
        }

        return "dashboarddetail";
    }
}
