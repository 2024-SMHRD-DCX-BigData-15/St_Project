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

    /**
     * 회원 정보를 DB에 저장
     * @param memberEntity 회원 정보
     */
    public void registerMember(MemberEntity memberEntity) {
        memberRepository.save(memberEntity);
    }

    /**
     * 아이디 중복 여부 확인
     * @param userId 클라이언트가 입력한 아이디
     * @return true(존재함), false(존재하지 않음)
     */
    public boolean isIdExist(String userId) {
        return memberRepository.existsById(userId);
    }

    /**
     * SHA-256 방식으로 비밀번호 암호화
     * @param password 원본 비밀번호
     * @return 암호화된 비밀번호
     */
    public String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }
}
