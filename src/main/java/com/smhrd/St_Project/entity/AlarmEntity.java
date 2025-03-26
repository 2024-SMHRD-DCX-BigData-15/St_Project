package com.smhrd.St_Project.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "t_alarm")
public class AlarmEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ 자동 증가 설정 추가
	@Column(name = "alarm_num", nullable = false)
	private Long alarmNum;

    @ManyToOne
    @JoinColumn(name = "tank_idx", nullable = false)
    private TankEntity tank;  // FK (INT UNSIGNED)

    @Column(name = "alarm_created_at", nullable = false)
    private Timestamp alarmCreatedAt;
    
    @Column(name = "alarm_msg", columnDefinition = "TEXT")
    private String alarmMsg;  // 알람 내용 (TEXT)
    
    @Column(name = "alarm_read", length = 1, nullable = false)
    private char alarmRead;
    
    @Column(name = "alarm_read_at", nullable = true)
    private Timestamp alarmReadAt;
    
    
}
