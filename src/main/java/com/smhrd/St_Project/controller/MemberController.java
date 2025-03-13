package com.smhrd.St_Project.controller;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.service.MemberService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 회원 가입 처리
     * @param memberEntity 클라이언트가 입력한 회원 정보
     * @return 로그인 페이지로 리다이렉트
     */
    @PostMapping("/register.do")
    public String register(@RequestParam("id") String userId,
                           @RequestParam("pw") String password,
                           @RequestParam("name") String userName,
                           @RequestParam("add") String userAdd,
                           @RequestParam("phone") String userPhone) {

        // 비밀번호 암호화
        String encryptedPassword = memberService.encryptPassword(password);

        // 회원 정보 객체 생성
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUserId(userId);
        memberEntity.setUserPw(encryptedPassword);
        memberEntity.setUserName(userName);
        memberEntity.setUserAdd(userAdd);
        memberEntity.setUserPhone(userPhone);
        memberEntity.setUserStatus('N');
        memberEntity.setUserRole('1');
        memberEntity.setJoinedAt(Timestamp.valueOf(LocalDateTime.now()));
        // 회원 정보 저장
        memberService.registerMember(memberEntity);

        System.out.println("✅ 회원 가입 성공: " + userId);

        return "redirect:/login";
    }

    /**
     * 로그인 처리
     * @param userId 사용자 입력 ID
     * @param password 사용자 입력 비밀번호
     * @param session 로그인 유지 세션
     * @return 로그인 성공 시 대시보드, 실패 시 로그인 페이지로 이동
     */
    @PostMapping("/login.do")
    public String login(@RequestParam("id") String userId,
                        @RequestParam("pw") String password,
                        HttpSession session) {

        // 비밀번호 암호화 후 DB에서 검증
        String encryptedPassword = memberService.encryptPassword(password);
        MemberEntity member = memberService.login(userId, encryptedPassword);

        if (member != null) {
            // 로그인 성공 -> 세션 저장 후 대시보드로 이동
            session.setAttribute("loginUser", member);
            System.out.println("✅ 로그인 성공: " + userId);
            return "redirect:/maindashboard";
        } else {
            // 로그인 실패 -> 로그인 페이지로 이동 + 오류 메시지 전달
            System.out.println("🚨 로그인 실패 (ID 또는 비밀번호 불일치): " + userId);
            return "redirect:/login?error=invalid";
        }
    }
    
    @PostMapping("/update")
    public String updateMember(@RequestParam("id") String userId,
                               @RequestParam(value = "pw", required = false) String password,
                               @RequestParam(value = "name", required = false) String userName,
                               @RequestParam(value = "add", required = false) String userAdd,
                               @RequestParam(value = "phone", required = false) String userPhone,
                               HttpSession session) {

        // 1. 현재 로그인한 사용자 정보 가져오기
        MemberEntity loginUser = (MemberEntity) session.getAttribute("loginUser");

        // 2. 로그인한 사용자가 없거나 다른 ID를 수정하려는 경우 차단
        if (loginUser == null || !loginUser.getUserId().equals(userId)) {
            System.out.println("🚨 접근 오류: 로그인 필요 또는 권한 없음");
            return "redirect:/login";
        }

        // 3. 기존 회원 정보 가져오기
        MemberEntity existingMember = memberService.findMemberById(userId);
        if (existingMember == null) {
            System.out.println("❌ 회원 정보 찾을 수 없음: " + userId);
            return "redirect:/edit/" + userId + "?error=notfound";
        }

        // 4. 변경된 값만 반영 (null 값은 기존 정보 유지)
        if (password != null && !password.isEmpty()) {
            existingMember.setUserPw(memberService.encryptPassword(password)); // 🔹 암호화 후 저장
        }
        if (userName != null && !userName.isEmpty()) {
            existingMember.setUserName(userName);
        }
        if (userAdd != null && !userAdd.isEmpty()) {
            existingMember.setUserAdd(userAdd);
        }
        if (userPhone != null && !userPhone.isEmpty()) {
            existingMember.setUserPhone(userPhone);
        }

        // 5. 변경된 정보 저장
        memberService.updateMember(existingMember);
        session.setAttribute("loginUser", existingMember); // 세션 정보도 업데이트

        System.out.println("✅ 회원 정보 수정 완료: " + userId);

        return "redirect:/maindashboard?update=success"; // 수정 후 대시보드로 이동
    }

}
