package com.smhrd.St_Project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.smhrd.St_Project.entity.MemberEntity;
import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
	
	boolean existsByUserId(String userId);
	
	// 로그인 기능
	MemberEntity findByUserIdAndUserPw(String userId, String userPw);

	// 회원 정보 수정 기능
	static void save(String userId) {		
	}
	
	// userId로 사용자 조회 (탈퇴 상태 포함 여부 확인)
	MemberEntity findByUserId(String userId);

	// userId와 userStatus로 회원 탈퇴 상태 확인
	MemberEntity findByUserIdAndUserStatus(String userId, char userStatus);
    
    // 3분 이상 지난 탈퇴 회원 조회 (탈퇴 시간이 기록되지 않았으므로 단순히 탈퇴일 기준)
	@Query(value = "SELECT * FROM t_user WHERE user_status = 'Y' AND deleted_at <= NOW() - INTERVAL 3 MINUTE", nativeQuery = true)
	List<MemberEntity> findOldInactiveUsers();

}
