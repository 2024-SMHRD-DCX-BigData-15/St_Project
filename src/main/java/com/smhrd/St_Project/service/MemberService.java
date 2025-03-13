package com.smhrd.St_Project.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    	  memberRepository.save(member);
      }

      // 아이디 중복체크 기능
      public boolean isUserIdExists(String id) {
         return memberRepository.existsByUserId(id);
      }
      
      // 로그인 기능 (암호화된 비밀번호 검증)
      public MemberEntity login(String id, String pw) {
          if (id == null || id.isEmpty() || pw == null || pw.isEmpty()) {
              System.out.println("로그인 실패: ID 또는 PW가 null 또는 비어 있음");
              return null;
          }

          String encryptedPw = PasswordEncryptor.encryptSHA256(pw);
          MemberEntity member = memberRepository.findByUserIdAndUserPw(id, encryptedPw);

          if (member == null) {
              System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못됨");
              return null;
          }

          if (member.getUserStatus() == 'Y') {
              System.out.println("로그인 실패: 해당 계정은 탈퇴 상태입니다.");
              return null;
          }

          return member;
      }

      public void updateMember(MemberEntity member) {
          memberRepository.save(member);
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
    	    
      public Optional<MemberEntity> findDetail(String id) {
          return memberRepository.findById(id);
      }

      public void findMemberById(String id) {
          Optional<MemberEntity> member = memberRepository.findById(id);
          
          if (member.isPresent()) {
              MemberEntity memberEntity = member.get();
              System.out.println(memberEntity);
          } else {
              System.out.println("Member not found");
          }
      }
      
      // 회원 탈퇴 처리
      public boolean deleteMember(String id, String pw) {
    	    String encryptedPw = PasswordEncryptor.encryptSHA256(pw);
    	    MemberEntity member = memberRepository.findByUserIdAndUserPw(id, encryptedPw);

    	    if (member == null) {
    	        return false;
    	    }

    	    // 탈퇴 처리
    	    member.setUserStatus('Y');
    	    member.setDeletedAt(new Timestamp(System.currentTimeMillis())); // 현재 시간 저장
    	    memberRepository.save(member);

    	    return true;
    	}

      // 탈퇴 버튼 클릭 후 일정 시간 이후 계정 삭제
      @Scheduled(fixedRate = 180000) // 3분(180,000ms)마다 실행 추후 30일(1000*60*60*24*30)로 변경
      @Transactional
      public void deleteInactiveUsers() {
          System.out.println("=== 3분 이상 지난 탈퇴 회원 삭제 시작 ===");

          List<MemberEntity> oldUsers = memberRepository.findOldInactiveUsers();

          System.out.println("삭제 대상 회원 수: " + oldUsers.size());

          for (MemberEntity user : oldUsers) {
              System.out.println("삭제 진행 중: " + user.getUserId());
              memberRepository.delete(user);
          }

          System.out.println("=== 3분 이상 지난 탈퇴 회원 삭제 완료 ===");
      }
      
      // 계정 복구 기능 추가
      public boolean recoverMember(String id, String pw) {
          String encryptedPw = PasswordEncryptor.encryptSHA256(pw);
          MemberEntity member = memberRepository.findByUserIdAndUserPw(id, encryptedPw);

          if (member == null || member.getUserStatus() == 'N') {
              System.out.println("계정 복구 실패: 존재하지 않거나 이미 활성화된 계정");
              return false;
          }

          // 계정 복구 처리
          member.setUserStatus('N');  // 정상 상태로 변경
          member.setDeletedAt(null);  // 삭제 날짜 초기화
          memberRepository.save(member);

          System.out.println("계정 복구 완료: " + id);
          return true;
      }
      

      // 🔹 ID로 사용자 조회 (비밀번호는 Java에서 비교)
      public MemberEntity freelogin(String id, String rawPw) {
    	    Optional<MemberEntity> optionalMember = memberRepository.findById(id);

    	    if (optionalMember.isPresent()) {
    	        MemberEntity member = optionalMember.get();

    	        // 🔹 비밀번호가 null인지 확인
    	        if (member.getUserPw() == null) {
    	            System.out.println("❌ DB에 저장된 비밀번호 없음!");
    	            return null;
    	        }

    	        // 🔹 사용자가 입력한 비밀번호를 SHA-256으로 암호화
    	        String hashedPw = PasswordEncryptor.encryptSHA256(rawPw);
    	        System.out.println("🔹 입력된 비밀번호 해시값: " + hashedPw);
    	        System.out.println("🔹 DB 저장된 비밀번호 해시값: " + member.getUserPw());

    	        // 🔹 올바르게 비교 수행
    	        if (hashedPw.equals(member.getUserPw())) {
    	            return member; // ✅ 로그인 성공
    	        } else {
    	            System.out.println("❌ 비밀번호 불일치");
    	        }
    	    }

    	    System.out.println("❌ 로그인 실패: 사용자 ID 없음");
    	    return null; // 로그인 실패
    	}



}
