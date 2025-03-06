package com.smhrd.St_Project.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "t_tank")
@Data
public class TankEntity {

	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "tank_idx", nullable = false)
    private Long tankIdx;

    @ManyToOne
    @JoinColumn (name = "user_id", nullable = false)
    private MemberEntity user;

    @Column(name = "tank_weight", precision = 12, scale = 1, nullable = false)
    private Double tankWeight;

    @Column(name = "tank_height", precision = 12, scale = 1, nullable = false)
    private Double tankHeight;

    @Column(name = "tank_location", length = 255)
    private String tankLocation;

    @Column(name = "fish_type", length = 255)
    private String fishType;

    @Column(name = "started_at", nullable = false)
    private Date startedAt;
    
    @OneToMany (mappedBy = "tank", cascade = CascadeType.ALL)
    private List<TankData> tankData;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL)
    private List<Env> envData;
	
	
}
