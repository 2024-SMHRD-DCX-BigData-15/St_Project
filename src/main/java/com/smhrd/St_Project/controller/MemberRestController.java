package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    /**
     * 아이디 중복 체크 API11
     * @param userId 클라이언트가 입력한 아이디
     * @return true(중복 없음), false(중복 있음)
     */
    @GetMapping("/checkId")
    public boolean checkId(@RequestParam String userId) {
        boolean isAvailable = !memberService.isIdExist(userId);

        // ✅ 디버깅 로그
        System.out.println("아이디 중복 체크 요청: " + userId + " -> 사용 가능 여부: " + isAvailable);

        return isAvailable;
    }
}
