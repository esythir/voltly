package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "TB_CONSUMPTION_LIMITS")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ConsumptionLimit {

    @Id @GeneratedValue(
            strategy = SEQUENCE,
            generator = "TB_CONSUMPTION_LIMITS_ID_SEQ")
    @SequenceGenerator(
            name = "TB_CONSUMPTION_LIMITS_ID_SEQ",
            sequenceName = "TB_CONSUMPTION_LIMITS_ID_SEQ",
            allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "limit_kwh", nullable = false)
    private Double limitKwh;

    @Column(name = "computed_at", nullable = false)
    private LocalDate computedAt;
}
