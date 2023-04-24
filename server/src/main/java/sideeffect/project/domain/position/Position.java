package sideeffect.project.domain.position;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_name", unique = true)
    private PositionType positionType;

    @Builder
    public Position(PositionType positionType) {
        this.positionType = positionType;
    }
}
