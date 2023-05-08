package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.position.Position;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "boardPosition")
    private List<Applicant> applicants;

    @Builder
    public BoardPosition(Long id, int targetNumber, Position position, RecruitBoard recruitBoard) {
        this.id = id;
        this.targetNumber = targetNumber;
        this.currentNumber = 0;
        this.position = position;
        this.recruitBoard = recruitBoard;
        this.applicants = new ArrayList<>();
    }

    public void setRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoard = recruitBoard;
    }

    public void addApplicant(Applicant applicant) {
        this.applicants.add(applicant);
    }

    public void increaseCurrentNumber() {
        if(this.currentNumber < this.targetNumber) {
            this.currentNumber++;
        }
    }

    public void decreaseCurrentNumber() {
        if(this.currentNumber > 0) {
            this.currentNumber--;
        }
    }

}
