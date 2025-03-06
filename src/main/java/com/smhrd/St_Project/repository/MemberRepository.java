package com.smhrd.St_Project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smhrd.St_Project.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	
	boolean existsById(String id);
	
	// 로그인 기능
	MemberEntity findByIdAndPw(String id, String pw);

}
