package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "TB_ALERTS")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_ALERTS_ID_SEQ")
    @SequenceGenerator(
            name = "TB_ALERTS_ID_SEQ",
            sequenceName = "TB_ALERTS_ID_SEQ",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;

    @Column(name = "consumption_kwh", nullable = false)
    private Double consumptionKwh;

    @Column(name = "limit_kwh", nullable = false)
    private Double limitKwh;

    @Column(name = "exceeded_by_kwh", nullable = false)
    private Double exceededByKwh;

    @Column(length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
