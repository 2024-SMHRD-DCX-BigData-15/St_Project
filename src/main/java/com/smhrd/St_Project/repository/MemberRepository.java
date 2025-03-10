package com.smhrd.St_Project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smhrd.St_Project.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
	
	boolean existsByUserId(String userId);
	
	// 로그인 기능
	MemberEntity findByUserIdAndUserPw(String userId, String userPw);

	// 회원 정보 수정 기능
	static void save(String userId) {		
	}
	
	// 회원 탈퇴 관련 메서드 추가
    // userId로 사용자 조회 (탈퇴 상태 포함 여부 확인)
    MemberEntity findByUserId(String userId);

    // userId와 userStatus로 회원 탈퇴 상태 확인
    MemberEntity findByUserIdAndUserStatus(String userId, char userStatus);
}