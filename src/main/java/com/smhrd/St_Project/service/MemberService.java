package com.smhrd.St_Project.service;

import com.smhrd.St_Project.entity.MemberEntity;
import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.util.PasswordEncryptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

 // 🔹 인증 토큰 저장 (토큰 - 사용자 ID 매핑)
    private static final ConcurrentHashMap<String, String> authTokenStorage = new ConcurrentHashMap<>();

    /**
     * 회원 정보를 DB에 저장
     * @param memberEntity 회원 정보
     */
    public void registerMember(MemberEntity memberEntity) {
        memberRepository.save(memberEntity);
        System.out.println("✅ 회원 가입 완료: " + memberEntity.getUserId());
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
        if (password == null || password.isEmpty()) {
            return null;
        }

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

    /**
     * 로그인 처리 (ID와 암호화된 비밀번호 검증)
     * @param userId 사용자 입력 ID
     * @param encryptedPassword 암호화된 비밀번호
     * @return 로그인 성공 시 MemberEntity 객체, 실패 시 null
     */
    public MemberEntity login(String userId, String encryptedPassword) {
        Optional<MemberEntity> member = memberRepository.findById(userId);

        if (member.isPresent() && member.get().getUserPw().equals(encryptedPassword)) {
            System.out.println("✅ 로그인 검증 성공: " + userId);
            return member.get();
        } else {
            System.out.println("🚨 로그인 검증 실패 (ID 또는 비밀번호 불일치): " + userId);
            return null;
        }
    }

    /**
     * 회원 정보 수정
     * @param member 수정할 회원 정보
     */
    public void updateMember(MemberEntity member) {
        System.out.println("📌 회원 정보 업데이트: " + member.getUserId());
        memberRepository.save(member);
    }

    /**
     * 특정 ID의 회원 정보 조회
     * @param userId 조회할 사용자 ID
     * @return 회원 정보 (없으면 null)
     */
    public MemberEntity findMemberById(String userId) {
        return memberRepository.findById(userId).orElse(null);
    }

    /**
     * 회원 탈퇴
     * @param userId 탈퇴할 사용자 ID
     * @param password 입력된 비밀번호
     * @return 탈퇴 성공 여부
     */
    public boolean deleteMember(String userId, String password) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(userId);

        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();

            // 📌 입력된 비밀번호 해싱 후 비교
            String encryptedPassword = PasswordEncryptor.encryptSHA256(password);

            // 📌 디버깅 로그
            System.out.println("[디버깅] DB 조회된 회원 정보: " + member.getUserId());
            System.out.println("[디버깅] 저장된 비밀번호: " + member.getUserPw()); 
            System.out.println("[디버깅] 입력된 비밀번호(암호화 후): " + encryptedPassword); 
            System.out.println("[디버깅] 현재 회원 상태: " + member.getUserStatus());

            if (member.getUserPw().equals(encryptedPassword)) { // ✅ 암호화된 비밀번호로 비교
                member.setUserStatus('Y'); // ✅ user_status 변경
                member.setDeletedAt(new Timestamp(System.currentTimeMillis())); // ✅ deletedAt에 현재 시간 저장
                memberRepository.save(member);

                System.out.println("[디버깅] 회원 상태 변경 완료 (user_status = 'Y')");
                System.out.println("[디버깅] 삭제 날짜 설정 완료: " + member.getDeletedAt());
                return true;
            } else {
                System.out.println("[디버깅] 비밀번호 불일치");
            }
        } else {
            System.out.println("[디버깅] 회원 정보 없음");
        }
        return false;
    }

    /**
     * 🔹 인증 토큰 생성 (Base64 인코딩)
     */
    public String generateAuthToken(MemberEntity member) {
        String token = Base64.getEncoder().encodeToString((member.getUserId() + ":" + System.currentTimeMillis()).getBytes());
        authTokenStorage.put(token, member.getUserId()); // ✅ 토큰 저장
        System.out.println("✅ 인증 토큰 생성: " + token);
        return token;
    }

    /**
     * 🔹 인증 토큰 검증 (탈퇴한 계정 확인 및 자동 삭제 기능 포함)
     * @param token 클라이언트에서 제공한 토큰
     * @return 검증된 사용자 (없으면 null)
     */
    public MemberEntity validateAuthToken(String token) {
        String userId = authTokenStorage.get(token);

        if (userId != null) {
            MemberEntity member = memberRepository.findById(userId).orElse(null);

            if (member != null) {
                // 🔹 탈퇴한 계정이면 자동 로그인 실패 및 토큰 삭제
                if (member.getUserStatus() == 'Y') {
                    System.out.println("🚨 유효하지 않은 토큰: 탈퇴한 계정 (" + userId + ")");
                    removeAuthToken(token);
                    return null;
                }

                System.out.println("✅ 유효한 토큰 확인: " + token);
                return member;
            }
        }

        System.out.println("🚨 유효하지 않은 토큰: " + token);
        removeAuthToken(token); // 🔥 유효하지 않은 토큰 삭제
        return null;
    }


    /**
     * 🔹 로그아웃 (토큰 삭제)
     */
    public void removeAuthToken(String token) {
        authTokenStorage.remove(token);
        System.out.println("✅ 로그아웃 완료 - 토큰 삭제: " + token);
    }
    
 // ✅ 계정 복구 로직
    public boolean recoverMember(String userId, String password) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(userId);

        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            String encryptedPassword = PasswordEncryptor.encryptSHA256(password);

            // 📌 디버깅 로그
            System.out.println("[디버깅] 계정 복구 요청 아이디: " + userId);
            System.out.println("[디버깅] 입력된 비밀번호(암호화 후): " + encryptedPassword);
            System.out.println("[디버깅] 저장된 비밀번호: " + member.getUserPw());

            if (member.getUserPw().equals(encryptedPassword)) {
                member.setUserStatus('N'); // ✅ 계정 활성화
                member.setDeletedAt(null); // ✅ 삭제 날짜 초기화
                memberRepository.save(member);

                System.out.println("[디버깅] 계정 복구 완료: user_status = 'N', deleted_at = NULL");
                return true;
            } else {
                System.out.println("[디버깅] 비밀번호 불일치");
            }
        } else {
            System.out.println("[디버깅] 회원 정보 없음");
        }
        return false;
    }

}
