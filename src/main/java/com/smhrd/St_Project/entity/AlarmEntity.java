package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "t_alarm")
public class AlarmEntity {

    @Id
    @Column(name = "alarm_num", nullable = false)
    private Long alarmNum;  // PK (INT UNSIGNED)

    @ManyToOne
    @JoinColumn(name = "tank_idx", nullable = false)
    private TankEntity tank;  // FK (INT UNSIGNED)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MemberEntity user;  // FK (VARCHAR(50))

    @Column(name = "alarm_msg", columnDefinition = "TEXT")
    private String alarmMsg;  // 알람 내용 (TEXT)
}
