package sideeffect.project.domain.freeboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;

@Entity
@Getter
@Table(
    name = "free_boards",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_project_url",
            columnNames = "project_url"
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeBoard extends BaseTimeEntity {

    @Id
    @Column(name = "free_board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int views;

    private String title;

    @Column(name = "project_url")
    private String projectUrl;

    private String content;

    private String imgUrl;

    private String projectName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "freeBoard")
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "freeBoard", orphanRemoval = true,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Like> likes;

    @Builder
    public FreeBoard(Long id, String title, String projectUrl, String content, String imgUrl, String projectName) {
        this.id = id;
        this.views = 0;
        this.title = title;
        this.projectUrl = projectUrl;
        this.content = content;
        this.imgUrl = imgUrl;
        this.projectName = projectName;
        this.comments = new ArrayList<>();
        this.likes = new HashSet<>();
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
        if (freeBoard.getProjectName() != null) {
            this.projectName = freeBoard.getProjectName();
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

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void deleteComment(Comment comment) {
        this.comments.remove(comment);
    }

    public void addLike(Like like) {
        this.likes.add(like);
    }

    public void deleteLike(Like like) {
        this.likes.remove(like);
    }
}
