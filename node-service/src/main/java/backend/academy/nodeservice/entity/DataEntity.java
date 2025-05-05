package backend.academy.nodeservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_key", unique = true, nullable = false)
    private String key;

    @Column(name = "data_value", nullable = false)
    private String value;
}
