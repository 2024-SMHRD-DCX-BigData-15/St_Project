package com.smhrd.St_Project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "t_tank")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ Hibernate 프록시 제거
public class TankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ tank_idx 자동 증가 설정
    @Column(name = "tank_idx", nullable = false)
    private Long tankIdx;  // PK

    @ManyToOne(fetch = FetchType.LAZY) // ✅ LAZY 로딩 적용
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore  // ✅ 무한 루프 방지
    private MemberEntity user;  // FK

    @Column(name = "tank_width", precision = 12, scale = 1)
    private BigDecimal tankWidth;

    @Column(name = "tank_height", precision = 12, scale = 1)
    private BigDecimal tankHeight;

    @Column(name = "tank_location", length = 255)
    private String tankLocation;

    @Column(name = "fish_type", length = 255)
    private String fishType;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ 무한 루프 방지
    private List<TankDataEntity> tankData;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ 무한 루프 방지
    private List<EnvEntity> environments;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ 무한 루프 방지
    private List<AlarmEntity> alarms;
    
    // 추가된 필드 (삭제 여부)
    @Column(name = "tank_delete", nullable = false)
    private String tank_delete = "N"; // 기본값은 false로 설정

    @Override
    public String toString() {
        return "TankEntity{" +
                "tankIdx=" + tankIdx +
                ", tankWidth=" + tankWidth +
                ", tankHeight=" + tankHeight +
                ", tankLocation='" + tankLocation + '\'' +
                ", fishType='" + fishType + '\'' +
                ", startedAt=" + startedAt +
                ", tank_delete='" + tank_delete + '\'' +  // 삭제 여부 추가
                '}';
    }
}
