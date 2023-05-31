package sideeffect.project.domain.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "RECRUIT_COMMENTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_board_id")
    private RecruitBoard recruitBoard;

    @Builder
    public RecruitComment(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public void associate(User user, RecruitBoard recruitBoard) {
        recruitBoard.addRecruitComment(this);
        this.user = user;
        this.recruitBoard = recruitBoard;
    }

    public void update(String content) {
        if (StringUtils.hasText(content)) {
            this.content = content;
        }
    }

}
