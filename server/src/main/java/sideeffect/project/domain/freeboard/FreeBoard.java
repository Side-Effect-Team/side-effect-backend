package sideeffect.project.domain.freeboard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int views;

    private String title;

    private String projectUrl;

    private String content;

    private String imgUrl;

    private Long userId;


    @Builder
    public FreeBoard(Long id, String title, String projectUrl, String content, String imgUrl, Long userId) {
        this.id = id;
        this.views = 0;
        this.title = title;
        this.projectUrl = projectUrl;
        this.content = content;
        this.imgUrl = imgUrl;
        this.userId = userId;
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

    public void setUser(Long userId) {
        this.userId = userId;
    }
}
