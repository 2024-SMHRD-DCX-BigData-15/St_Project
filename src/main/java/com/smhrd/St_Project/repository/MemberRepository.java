package com.smhrd.St_Project.repository;

import com.smhrd.St_Project.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    /**
     * ID와 암호화된 비밀번호가 일치하는지 확인하는 쿼리
     */
    @Query("SELECT m FROM MemberEntity m WHERE m.userId = :userId AND m.userPw = :userPw")
    MemberEntity findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);
}
