package br.com.fiap.voltly.domain.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "TB_SENSORS")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Sensor {

    @Id @GeneratedValue(
            strategy = SEQUENCE,
            generator = "TB_SENSORS_ID_SEQ")
    @SequenceGenerator(
            name = "TB_SENSORS_ID_SEQ",
            sequenceName = "TB_SENSORS_ID_SEQ",
            allocationSize = 1)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true, length = 100)
    private String serialNumber;

    @Column(nullable = false, length = 80)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
}
