package br.com.fiap.voltly.domain.model;

import br.com.fiap.voltly.enums.EfficiencyRating;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_DAILY_REPORTS")
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyReport {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "TB_DAILY_REPORTS_ID_SEQ"
    )
    @SequenceGenerator(
            name = "TB_DAILY_REPORTS_ID_SEQ",
            sequenceName = "TB_DAILY_REPORTS_ID_SEQ",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "consumption_kwh", nullable = false)
    private Double consumptionKwh;

    @Column(name = "co2_emission_kg", nullable = false)
    private Double co2EmissionKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "efficiency_rating", length = 20, nullable = false)
    private EfficiencyRating efficiencyRating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
