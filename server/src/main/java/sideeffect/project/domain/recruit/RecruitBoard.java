package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RECRUIT_BOARD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_board_id")
    private Long id;

    private String title;

    private String contents;

    private int views;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_board_type")
    private RecruitBoardType recruitBoardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_type")
    private ProgressType progressType;

    @Column(name = "expected_period")
    private String expectedPeriod;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recruitBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BoardPosition> boardPositions = new ArrayList<>();

    @OneToMany(mappedBy = "recruitBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BoardStack> boardStacks = new ArrayList<>();

    @Builder
    public RecruitBoard(Long id, String title, String contents, RecruitBoardType recruitBoardType, ProgressType progressType, String expectedPeriod, LocalDateTime deadline) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.views = 0;
        this.recruitBoardType = recruitBoardType;
        this.progressType = progressType;
        this.expectedPeriod = expectedPeriod;
        this.deadline = deadline;
    }

    public void updateBoardPositions(List<BoardPosition> boardPositions) {
        this.boardPositions.clear();
        this.boardPositions.addAll(boardPositions);
    }

    public void updateBoardStacks(List<BoardStack> boardStacks) {
        this.boardStacks.clear();
        this.boardStacks.addAll(boardStacks);
    }

    public void addBoardPosition(BoardPosition boardPosition) {
        this.boardPositions.add(boardPosition);
    }

    public void addBoardStack(BoardStack boardStack) {
        this.boardStacks.add(boardStack);
    }

    public void update(RecruitBoard recruitBoard) {
        if(recruitBoard.getTitle() != null) {
            this.title = recruitBoard.getTitle();
        }
        if(recruitBoard.getContents() != null) {
            this.contents = recruitBoard.getContents();
        }
        if(recruitBoard.getRecruitBoardType() != null) {
            this.recruitBoardType = recruitBoard.getRecruitBoardType();
        }
        if(recruitBoard.getProgressType() != null) {
            this.progressType = recruitBoard.getProgressType();
        }
        if(recruitBoard.getExpectedPeriod() != null) {
            this.expectedPeriod = recruitBoard.getExpectedPeriod();
        }
        if(recruitBoard.getDeadline() != null) {
            this.deadline = recruitBoard.getDeadline();
        }
    }

    public void increaseViews() {
        this.views++;
    }

    public void associateUser(User user) {
        if (this.user != null) {
            this.user.deleteRecruitBoard(this);
        }
        user.addRecruitBoard(this);
        this.user = user;
    }

}
