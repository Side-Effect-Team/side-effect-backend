package sideeffect.project.domain.recommend;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;

@Getter
@Entity
@Table(
    indexes = {
        @Index(name = "board_index", columnList = "free_board_id")
    }
)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_board_id")
    private FreeBoard freeBoard;

    public static Recommend recommend(User user, FreeBoard freeBoard) {
        Recommend recommend = new Recommend();
        recommend.setUser(user);
        recommend.setFreeBoard(freeBoard);
        return recommend;
    }

    public void setUser(User user) {
        this.user = user;
        user.addRecommend(this);
    }

    public void setFreeBoard(FreeBoard freeBoard) {
        this.freeBoard = freeBoard;
        freeBoard.addRecommend(this);
    }
}
