package sideeffect.project.domain.user;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.RecruitBoard;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"email"}
                )
        }
)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String introduction;
    private PositionType position;
    private String career;

    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    private String imgUrl;

    private String blogUrl;
    private String githubUrl;
    private String portfolioUrl;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id desc")
    private List<FreeBoard> freeBoards = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id desc")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id desc")
    private List<RecruitBoard> recruitBoards = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Applicant> applicants = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "user")
    private Set<Like> likes = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<RecruitLike> recruitLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<UserStack> userStacks = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, String nickname, String introduction, PositionType position, String career, UserRoleType userRoleType, ProviderType providerType, String imgUrl, String blogUrl, String githubUrl, String portfolioUrl) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.introduction = introduction;
        this.position = position;
        this.career = career;
        this.userRoleType = userRoleType;
        this.providerType = providerType;
        this.imgUrl = imgUrl;
        this.blogUrl = blogUrl;
        this.githubUrl = githubUrl;
        this.portfolioUrl = portfolioUrl;
    }

    public void addFreeBoard(FreeBoard freeBoard) {
        this.freeBoards.add(freeBoard);
    }

    public void deleteFreeBoard(FreeBoard freeBoard) {
        this.freeBoards.remove(freeBoard);
    }

    public void addRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoards.add(recruitBoard);
    }

    public void deleteRecruitBoard(RecruitBoard recruitBoard) {
        this.recruitBoards.remove(recruitBoard);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void deleteComment(Comment comment) {
        this.comments.remove(comment);
    }

    public void addApplicant(Applicant applicant) {
        this.applicants.add(applicant);
    }

    public void deleteApplicant(Applicant applicant) {
        this.applicants.remove(applicant);
    }

    public void addLike(Like like) {
        this.likes.add(like);
    }

    public void deleteLike(Like like) {
        this.likes.remove(like);
    }

    public void addRecruitLike(RecruitLike recruitLike) {
        this.recruitLikes.add(recruitLike);
    }

    public void deleteRecruitLike(RecruitLike recruitLike) {
        this.recruitLikes.remove(recruitLike);
    }

    public void update(User user){
        if(user.getNickname() != null) {
            this.nickname = user.getNickname();
        }
        if(user.getIntroduction() != null) {
            this.introduction = user.getIntroduction();
        }
        if(user.getPosition() != null) {
            this.position = user.getPosition();
        }
        if(user.getCareer() != null) {
            this.career = user.getCareer();
        }
        if(user.getImgUrl() != null) {
            this.imgUrl = user.getImgUrl();
        }
        if(user.getBlogUrl() != null) {
            this.blogUrl = user.getBlogUrl();
        }
        if(user.getGithubUrl() != null) {
            this.githubUrl = user.getGithubUrl();
        }
        if(user.getPortfolioUrl() != null) {
            this.portfolioUrl = user.getPortfolioUrl();
        }
    }

    public void updateUserStack(List<UserStack> userStacks){
        if(userStacks!=null && !userStacks.isEmpty()){
            this.userStacks.clear();
            this.userStacks.addAll(userStacks);
        }
    }
}
