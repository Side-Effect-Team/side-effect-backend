package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;

import javax.persistence.*;

@Entity
@Table(name = "BOARD_STACK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_stack_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private StackLevelType stackLevelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_board_id")
    private RecruitBoard recruitBoard;

    @Builder
    public BoardStack(StackLevelType stackLevelType, Stack stack, RecruitBoard recruitBoard) {
        this.stackLevelType = stackLevelType;
        this.stack = stack;
        this.recruitBoard = recruitBoard;
    }

    public void addRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoard = recruitBoard;
    }

}
