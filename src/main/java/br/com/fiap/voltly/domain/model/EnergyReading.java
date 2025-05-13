package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "TB_ENERGY_READINGS")
@Getter @Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class EnergyReading {

    @Id @GeneratedValue(strategy = SEQUENCE,
            generator = "TB_ENERGY_READINGS_ID_SEQ")
    @SequenceGenerator(name = "TB_ENERGY_READINGS_ID_SEQ",
            sequenceName = "TB_ENERGY_READINGS_ID_SEQ",
            allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(name = "power_kw", nullable = false)
    private Double powerKw;

    @Column(name = "occupancy_pct", nullable = false)
    private Double occupancyPct;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;
}
