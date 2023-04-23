package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruit_board")
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
    private RecruitBoardType recruitBoardType;

    @Enumerated(EnumType.STRING)
    private ProgressType progressType;

    @Column(name = "expected_period")
    private String expectedPeriod;

    private LocalDateTime deadline;

    @OneToMany(mappedBy = "recruitBoard")
    private List<BoardPosition> boardPositions = new ArrayList<>();

    @OneToMany(mappedBy = "recruitBoard")
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
}
