package sideeffect.project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.position.Position;

import javax.persistence.*;

@Entity
@Table(name = "USER_POSITION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPosition {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "career_years")
    private String careerYears;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserPosition(Long id, String careerYears, Position position, User user){
        this.id = id;
        this.careerYears = careerYears;
        this.position = position;
        this.user = user;
    }


}
