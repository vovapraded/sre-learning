package backend.academy.nodeservice.model;

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

    @Column(unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;
}
