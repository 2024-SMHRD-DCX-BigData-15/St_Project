package com.smhrd.St_Project.controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.entity.MemberEntity;

import jakarta.servlet.http.HttpSession;

import com.smhrd.St_Project.entity.MemberEntity;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;

	// 회원가입 기능 (비밀번호 암호화 적용)
    @PostMapping("/register.do")
    public String register(@RequestParam String id, @RequestParam String pw, 
                           @RequestParam String name, @RequestParam String add, 
                           @RequestParam String phone, @RequestParam char status, 
                           @RequestParam char role, @RequestParam Timestamp joinedat) {

        MemberEntity member = new MemberEntity();
        member.setUserId(id);
        member.setUserPw(pw); // 암호화는 Service에서 처리
        member.setUserName(name);
        member.setUserAdd(add);
        member.setUserPhone(phone);
        member.setUserStatus(status);
        member.setUserRole(role);
        member.setJoinedAt(joinedat);

        memberService.register(member);

        return "login"; // 회원가입 후 로그인 페이지로 이동
    }

    @PostMapping("/login.do")
    public String login(@RequestParam(required = false) String id, 
                        @RequestParam(required = false) String pw, 
                        HttpSession session) {

        // id 또는 pw 값이 없는 경우 예외 처리
        if (id == null || id.isEmpty() || pw == null || pw.isEmpty()) {
            System.out.println("로그인 실패: ID 또는 비밀번호가 비어있음");
            return "redirect:/login?error=missing_credentials";
        }

        System.out.println("로그인 요청: ID=" + id + ", PW=" + pw);

        MemberEntity member = memberService.login(id, pw);

        if (member != null) {
            session.setAttribute("loginUser", member);
            System.out.println("로그인 성공: " + id);
            // 세션에 저장된 로그인 사용자 정보 출력 (디버깅용)
            System.out.println("세션에 저장된 로그인 사용자: " + session.getAttribute("loginUser"));
            return "redirect:/maindashboard";
        } else {
            System.out.println("로그인 실패: 사용자 없음");
            return "redirect:/login?error=true";
        }
    }
    
    // 생성자를 통해 MemberService를 주입받음
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        // memberService 객체를 통해 findDetail 메서드 호출
        Optional<MemberEntity> member = memberService.findDetail(id);

        // Optional을 사용하여 값을 안전하게 처리
        member.ifPresent(memberEntity -> model.addAttribute("member", memberEntity));

        // "edit" 뷰로 전달
        return "edit";
    }


	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loginUser");

		return "redirect:/";
	}

}
