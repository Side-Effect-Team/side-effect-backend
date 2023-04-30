package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.position.Position;

import javax.persistence.*;

@Entity
@Table(name = "BOARD_POSITION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_position_id")
    private Long id;

    @Column(name = "target_number")
    private int targetNumber;

    @Column(name = "current_number")
    private int currentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_board_id")
    private RecruitBoard recruitBoard;

    @Builder
    public BoardPosition(int targetNumber, Position position, RecruitBoard recruitBoard) {
        this.targetNumber = targetNumber;
        this.currentNumber = 0;
        this.position = position;
        this.recruitBoard = recruitBoard;
    }

    public void addRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoard = recruitBoard;
    }

}
