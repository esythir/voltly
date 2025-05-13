package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "TB_AUTOMATIC_ACTIONS")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class AutomaticAction {

    @Id @GeneratedValue(
            strategy = SEQUENCE,
            generator = "TB_AUTOMATIC_ACTIONS_ID_SEQ")
    @SequenceGenerator(
            name = "TB_AUTOMATIC_ACTIONS_ID_SEQ",
            sequenceName = "TB_AUTOMATIC_ACTIONS_ID_SEQ",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 500)
    private String details;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;
}
