package com.smhrd.St_Project.entity;

import java.sql.Timestamp;
import java.util.List;

import com.smhrd.St_Project.entity.MemberEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table (name = "t_user")
public class MemberEntity {

	
	@Id
    @Column (name = "user_id", length = 50, nullable = false)
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
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Tank> tanks;
	
}
