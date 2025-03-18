package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@Table(name = "t_tankdata")
public class TankDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ 자동 증가 설정
    @Column(name = "record_num", nullable = false)
    private Long recordNum;  // PK (INT UNSIGNED)

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "tank_idx", nullable = false)
    @JsonBackReference // ✅ TankEntity의 직렬화 무한루프 방지
    private TankEntity tank;  // FK (INT UNSIGNED)

    @Column(name = "water_ph", precision = 7, scale = 5)
    private BigDecimal waterPh;  // 산성도 (DECIMAL(7,6))

    @Column(name = "water_do", precision = 7, scale = 5)
    private BigDecimal waterDo;  // 용존산소 (DECIMAL(7,6))

    @Column(name = "water_temp", precision = 7, scale = 5)
    private BigDecimal waterTemp;  // 수온 (DECIMAL(8,6))

    @Column(name = "water_salt", precision = 7, scale = 5)
    private BigDecimal waterSalt;  // 염도 (DECIMAL(7,6))

    @Column(name = "water_ammonia", precision = 7, scale = 5)
    private BigDecimal waterAmmonia;  // 암모니아 (DECIMAL(4,3))

    @Column(name = "water_nitrogen", precision = 7, scale = 5)
    private BigDecimal waterNitrogen;  // 아질산 (DECIMAL(4,3))

    @Column(name = "record_date", nullable = false)
    private Timestamp recordDate;  // 기록 날짜 (TIMESTAMP)
}
