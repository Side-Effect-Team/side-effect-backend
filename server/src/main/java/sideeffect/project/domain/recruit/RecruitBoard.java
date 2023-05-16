package sideeffect.project.domain.recruit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RECRUIT_BOARD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_board_id")
    private Long id;

    private String title;

    @Column(name = "project_name")
    private String projectName;

    private String contents;

    @Column(name = "img_src")
    private String imgSrc;

    private int views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recruitBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BoardPosition> boardPositions = new ArrayList<>();

    @OneToMany(mappedBy = "recruitBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BoardStack> boardStacks = new ArrayList<>();

    @OneToMany(mappedBy = "recruitBoard", cascade = {CascadeType.REMOVE})
    private List<RecruitLike> recruitLikes = new ArrayList<>();

    @Builder
    public RecruitBoard(Long id, String title, String projectName, String contents, String imgSrc) {
        this.id = id;
        this.title = title;
        this.projectName = projectName;
        this.contents = contents;
        this.imgSrc = imgSrc;
        this.views = 0;
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
        boardPosition.setRecruitBoard(this);
    }

    public void addBoardStack(BoardStack boardStack) {
        this.boardStacks.add(boardStack);
        boardStack.setRecruitBoard(this);
    }

    public void addRecruitLike(RecruitLike recruitLike) {
        this.recruitLikes.add(recruitLike);
    }

    public void update(RecruitBoard recruitBoard) {
        if(recruitBoard.getTitle() != null) {
            this.title = recruitBoard.getTitle();
        }
        if(recruitBoard.getProjectName() != null) {
            this.projectName = recruitBoard.getProjectName();
        }
        if(recruitBoard.getContents() != null) {
            this.contents = recruitBoard.getContents();
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
