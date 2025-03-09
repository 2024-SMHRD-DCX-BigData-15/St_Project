package com.smhrd.St_Project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.util.PasswordEncryptor;
import com.smhrd.St_Project.entity.MemberEntity;

@Service
public class MemberService {

      // repository 연결
      @Autowired()
      MemberRepository memberRepository;
      
   // 회원가입 기능 (비밀번호 암호화 적용)
      public String register(MemberEntity member) {
          // 비밀번호를 SHA-256으로 암호화
          String encryptedPw = PasswordEncryptor.encryptSHA256(member.getUserPw());
          member.setUserPw(encryptedPw);
          
          // 저장
          memberRepository.save(member);
          return "회원가입 성공";
      }
      
      // 아이디 중복체크 기능
      public boolean isUserIdExists(String id) {
         // 기능 추가
         // id 중복체크!!
         // existsById -> member pk idx
         // 실제 id라는 컬럼을 희망
         // true -> 데이터가 있다 --> 아이디가 중복되었다!
         return memberRepository.existsByUserId(id);
      }
      
   // 로그인 기능 (암호화된 비밀번호 검증)
      public MemberEntity login(String id, String pw) {
          String encryptedPw = PasswordEncryptor.encryptSHA256(pw);
          return memberRepository.findByUserIdAndUserPw(id, encryptedPw);
      }
     
}
