package com.smhrd.St_Project.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.util.PasswordEncryptor;

import jakarta.transaction.Transactional;

import com.smhrd.St_Project.entity.MemberEntity;

@Service
public class MemberService {

      // repository 연결
      @Autowired()
      MemberRepository memberRepository;
      
   // 회원가입 기능 (비밀번호 암호화 적용)
      public void register(MemberEntity member) {
    	    // 클라이언트에서 이미 해싱한 비밀번호를 전달했으므로, 추가 해싱 없이 저장
    	    memberRepository.save(member);
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
          if (id == null || id.isEmpty() || pw == null || pw.isEmpty()) {
              System.out.println("로그인 실패: ID 또는 PW가 null 또는 비어 있음");
              return null;
          }

          System.out.println("로그인 시도: ID=" + id + ", PW=" + pw);

          // 비밀번호를 SHA-256으로 암호화
          String encryptedPw = PasswordEncryptor.encryptSHA256(pw);

          System.out.println("암호화된 PW: " + encryptedPw);

          // 사용자 조회 (아이디와 암호화된 비밀번호로 비교)
          MemberEntity member = memberRepository.findByUserIdAndUserPw(id, encryptedPw);

          if (member == null) {
              System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못됨");
              return null;
          }

          // 탈퇴 여부 체크: userStatus가 'Y'이면 로그인 실패 처리
          if (member.getUserStatus() == 'Y') {
              System.out.println("로그인 실패: 해당 계정은 탈퇴 상태입니다.");
              return null;
          }

          System.out.println("로그인 성공: 아이디=" + member.getUserId());
          return member;
      }

      
      
      public void updateMember(MemberEntity member) {
          memberRepository.save(member);  // DB에 업데이트
      }

      public String encryptPassword(String password) {
          try {
              MessageDigest md = MessageDigest.getInstance("SHA-256");
              byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
              StringBuilder sb = new StringBuilder();
              for (byte b : hashedBytes) {
                  sb.append(String.format("%02x", b));
              }
              return sb.toString();
          } catch (NoSuchAlgorithmException e) {
              throw new RuntimeException("암호화 오류", e);
          }
      }
    	    
      // findDetail 메서드 추가
      public Optional<MemberEntity> findDetail(String id) {
          return memberRepository.findById(id);
      }

      // 기존의 findMemberById 메서드도 그대로 사용할 수 있습니다.
      public void findMemberById(String id) {
          Optional<MemberEntity> member = memberRepository.findById(id);
          
          if (member.isPresent()) {
              // 회원 정보가 존재하는 경우 처리
              MemberEntity memberEntity = member.get();
              System.out.println(memberEntity);
          } else {
              // 회원이 존재하지 않음
              System.out.println("Member not found");
          }
      }
      
      // 회원 탈퇴 처리
      public boolean deleteMember(String id, String pw) {
          // 비밀번호를 SHA-256으로 암호화
          String encryptedPw = PasswordEncryptor.encryptSHA256(pw);
          System.out.println("암호화된 비밀번호: " + encryptedPw);

          // 사용자 조회 (아이디와 암호화된 비밀번호로 비교)
          MemberEntity member = memberRepository.findByUserIdAndUserPw(id, encryptedPw);
          if (member == null) {
              // 사용자 정보가 없으면 탈퇴 실패
              System.out.println("탈퇴 실패: 아이디 또는 비밀번호가 잘못됨");
              return false;
          }

          // 탈퇴 처리 (userStatus를 'Y'로 변경)
          member.setUserStatus('Y');
          memberRepository.save(member);  // 변경 사항 저장

          System.out.println("사용자 탈퇴 완료: " + member.getUserId());

          return true;  // 탈퇴 완료
      }
  }
