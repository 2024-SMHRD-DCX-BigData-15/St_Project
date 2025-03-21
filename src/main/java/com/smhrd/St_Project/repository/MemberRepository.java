package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.MemberEntity;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    /**
     * ID와 암호화된 비밀번호가 일치하는지 확인하는 쿼리
     */
    @Query("SELECT m FROM MemberEntity m WHERE m.userId = :userId AND m.userPw = :userPw")
    MemberEntity findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);

    @Modifying
    @Query("UPDATE MemberEntity m SET m.userPw = :userPw, m.userName = :userName, m.userAdd = :userAdd, m.userPhone = :userPhone WHERE m.userId = :userId")
    void updateMember(@Param("userId") String userId,
                      @Param("userPw") String userPw,
                      @Param("userName") String userName,
                      @Param("userAdd") String userAdd,
                      @Param("userPhone") String userPhone);

    
    // ✅ 승인 대기 중인 회원 목록 조회 (user_status가 "Y" 또는 "N"인 모든 회원 가져오기)
    List<MemberEntity> findByUserStatusIn(List<String> statuses);

    // ✅ 회원 승인 (user_status를 "N"으로 변경)
    @Modifying
    @Transactional
    @Query("UPDATE MemberEntity m SET m.userStatus = 'N' WHERE m.userId = :userId AND m.userStatus = 'Y'")
    void approveUser(String userId);

    // ✅ 회원 거부 (user_status를 "Y"로 변경)
    @Modifying
    @Transactional
    @Query("UPDATE MemberEntity m SET m.userStatus = 'Y' WHERE m.userId = :userId AND m.userStatus = 'N'")
    void rejectUser(String userId);
}
