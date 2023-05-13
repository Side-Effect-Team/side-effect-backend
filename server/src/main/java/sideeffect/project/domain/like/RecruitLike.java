package sideeffect.project.domain.like;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;

import javax.persistence.*;

@Getter
@Entity
@Table(
        name = "RECRUIT_LIKES"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_board_id")
    private RecruitBoard recruitBoard;

    public static RecruitLike createRecruitLike(User user, RecruitBoard recruitBoard) {
        RecruitLike recruitLike = new RecruitLike();
        recruitLike.setUser(user);
        recruitLike.setRecruitBoard(recruitBoard);

        return recruitLike;
    }

    public void setUser(User user) {
        this.user = user;
        user.addRecruitLike(this);
    }

    public void setRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoard = recruitBoard;
        recruitBoard.addRecruitLike(this);
    }

}
