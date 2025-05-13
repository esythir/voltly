package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "TB_EQUIPMENTS")
@Getter @Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Equipment {

    @Id @GeneratedValue(
            strategy = SEQUENCE,
            generator = "TB_EQUIPMENTS_ID_SEQ")
    @SequenceGenerator(
            name = "TB_EQUIPMENTS_ID_SEQ",
            sequenceName = "TB_EQUIPMENTS_ID_SEQ",
            allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 250)
    private String description;

    @Column(name = "daily_limit_kwh", nullable = false)
    private Double dailyLimitKwh;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "equipment",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Sensor> sensors = new ArrayList<>();
}
