package com.smhrd.St_Project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smhrd.St_Project.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
	
	boolean existsByUserId(String userId);
	
	// 로그인 기능
	MemberEntity findByUserIdAndUserPw(String userId, String userPw);

}
