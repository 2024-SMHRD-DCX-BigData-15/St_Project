package com.smhrd.St_Project.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "t_tank")
public class TankEntity {

    @Id
    @Column(name = "tank_idx", nullable = false)
    private Long tankIdx;  // PK (INT UNSIGNED)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MemberEntity user;  // FK (VARCHAR(50))

    @Column(name = "tank_width", precision = 12, scale = 1)
    private BigDecimal tankWidth;  // 수조 직경 (DECIMAL(12,1))

    @Column(name = "tank_height", precision = 12, scale = 1)
    private BigDecimal tankHeight;  // 수조 높이 (DECIMAL(12,1))

    @Column(name = "tank_location", length = 255)
    private String tankLocation;  // 수조 위치 (VARCHAR(255))

    @Column(name = "fish_type", length = 255)
    private String fishType;  // 품종 (VARCHAR(255))

    @Column(name = "started_at")
    private Date startedAt;  // 사육개시일 (DATE)

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TankDataEntity> tankData;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvEntity> environments;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmEntity> alarms;
}
