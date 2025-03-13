package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // ID 중복 체크 API (AJAX 요청 처리)
    @GetMapping("/checkId")
    public boolean checkId(@RequestParam("id") String userId) {
        System.out.println("API - ID 중복 체크 요청: " + userId);
        return memberService.isIdAvailable(userId);
    }
}
