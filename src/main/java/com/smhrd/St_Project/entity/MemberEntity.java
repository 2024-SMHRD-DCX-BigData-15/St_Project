package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "t_user")
public class MemberEntity {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;  // PK (VARCHAR(50))

    @Column(name = "user_pw", length = 128, nullable = false)
    private String userPw;  // 비밀번호 (VARCHAR(128))

    @Column(name = "user_name", length = 10, nullable = false)
    private String userName;  // 회원이름 (VARCHAR(10))

    @Column(name = "user_add", length = 1000)
    private String userAdd;  // 양식장주소 (VARCHAR(1000))

    @Column(name = "user_phone", length = 20)
    private String userPhone;  // 회원연락처 (VARCHAR(20))

    @Column(name = "user_status", length = 1, nullable = false)
    private char userStatus;  // 탈퇴여부 (CHAR(1))

    @Column(name = "user_role", length = 1, nullable = false)
    private char userRole;  // 회원유형 (CHAR(1))

    @Column(name = "joined_at", nullable = false)
    private Timestamp joinedAt;  // 가입날짜 (TIMESTAMP)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TankEntity> tanks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmEntity> alarms;
}
