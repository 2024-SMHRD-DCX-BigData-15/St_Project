package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    // 회원가입 처리
    public boolean registerMember(MemberEntity member) {
        try {
            System.out.println("회원가입 서비스 실행: " + member.getUserId());
            
            // 비밀번호 해싱 적용 (SHA-256)
            String hashedPw = hashPassword(member.getUserPw());
            member.setUserPw(hashedPw);

            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            System.out.println("회원가입 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // ID 중복 체크
    public boolean isIdAvailable(String userId) {
        boolean isAvailable = !memberRepository.existsByUserId(userId);
        System.out.println("ID 사용 가능 여부: " + isAvailable);
        return isAvailable;
    }

    // 비밀번호 해싱 메서드 (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해싱 실패", e);
        }
    }
}
