package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "t_env")
public class EnvEntity {

    @Id
    @Column(name = "env_idx", nullable = false)
    private Long envIdx;  // PK (INT UNSIGNED)

    @ManyToOne
    @JoinColumn(name = "tank_idx", nullable = false)
    private TankEntity tank;  // FK (INT UNSIGNED)

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;  // 측정 날짜 (TIMESTAMP)

    @Column(name = "tank_density", precision = 10, scale = 4)
    private Double tankDensity;  // 밀도 (DECIMAL(10,4))

    @Column(name = "tank_temp", precision = 4, scale = 1)
    private Double tankTemp;  // 온도 (DECIMAL(4,1))

    @Column(name = "tank_population", precision = 12, scale = 1)
    private Double tankPopulation;  // 개체수 (DECIMAL(12,1))
}
