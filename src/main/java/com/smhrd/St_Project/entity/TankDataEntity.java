package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "t_tankdata")
public class TankDataEntity {

    @Id
    @Column(name = "record_num", nullable = false)
    private Long recordNum;  // PK (INT UNSIGNED)

    @ManyToOne
    @JoinColumn(name = "tank_idx", nullable = false)
    private TankEntity tank;  // FK (INT UNSIGNED)

    @Column(name = "water_ph", precision = 7, scale = 6)
    private Double waterPh;  // 산성도 (DECIMAL(7,6))

    @Column(name = "water_do", precision = 7, scale = 6)
    private Double waterDo;  // 용존산소 (DECIMAL(7,6))

    @Column(name = "water_temp", precision = 8, scale = 6)
    private Double waterTemp;  // 수온 (DECIMAL(8,6))

    @Column(name = "water_salt", precision = 7, scale = 6)
    private Double waterSalt;  // 염도 (DECIMAL(7,6))

    @Column(name = "water_ammonia", precision = 4, scale = 3)
    private Double waterAmmonia;  // 암모니아 (DECIMAL(4,3))

    @Column(name = "water_nitrogen", precision = 4, scale = 3)
    private Double waterNitrogen;  // 아질산 (DECIMAL(4,3))

    @Column(name = "record_date", nullable = false)
    private Timestamp recordDate;  // 기록 날짜 (TIMESTAMP)
}
