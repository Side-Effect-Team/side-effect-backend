package sideeffect.project.domain.position;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
        name = "POSITIONS",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "position_name"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_name")
    private PositionType positionType;

    @Builder
    public Position(Long id, PositionType positionType) {
        this.id = id;
        this.positionType = positionType;
    }
}
