package com.smhrd.St_Project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String userId;

    @Column(name = "user_pw", length = 128, nullable = false)
    private String userPw;

    @Column(name = "user_name", length = 10, nullable = false)
    private String userName;

    @Column(name = "user_add", length = 1000)
    private String userAdd;

    @Column(name = "user_phone", length = 20)
    private String userPhone;

    @Column(name = "user_status", length = 1, nullable = false)
    private char userStatus;

    @Column(name = "user_role", length = 1, nullable = false)
    private char userRole;

    @Column(name = "joined_at", nullable = false)
    private Timestamp joinedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ 무한 루프 방지
    private List<TankEntity> tanks;

    @Column(name = "deleted_at", nullable = true)
    private Timestamp deletedAt;

    @Override
    public String toString() {
        return "MemberEntity{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userAdd='" + userAdd + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userStatus=" + userStatus +
                ", userRole=" + userRole +
                ", joinedAt=" + joinedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
