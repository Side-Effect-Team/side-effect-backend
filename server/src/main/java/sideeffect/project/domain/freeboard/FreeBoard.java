package sideeffect.project.domain.freeboard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.user.User;

@Entity
@Getter
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = "project_url"
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int views;

    private String title;

    @Column(name = "project_url")
    private String projectUrl;

    private String content;

    private String imgUrl;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public FreeBoard(Long id, String title, String projectUrl, String content, String imgUrl, Long userId) {
        this.id = id;
        this.views = 0;
        this.title = title;
        this.projectUrl = projectUrl;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public void update(FreeBoard freeBoard) {
        if (freeBoard.getContent() != null) {
            this.content = freeBoard.getContent();
        }
        if (freeBoard.getTitle() != null) {
            this.title = freeBoard.getTitle();
        }
        if (freeBoard.getProjectUrl() != null) {
            this.projectUrl = freeBoard.getProjectUrl();
        }
    }

    public void increaseViews() {
        this.views++;
    }

    public void changeImageUrl(String url) {
        this.imgUrl = url;
    }

    public void deleteImageUrl() {
        this.imgUrl = null;
    }

    public void associateUser(User user) {
        if (this.user != null) {
            this.user.deleteFreeBoard(this);
        }
        user.addFreeBoard(this);
        this.user = user;
    }
}
