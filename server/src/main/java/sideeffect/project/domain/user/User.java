package sideeffect.project.domain.user;

import lombok.*;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.recruit.RecruitBoard;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    private String name;

    private String password;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private LocalDateTime deleteAt;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    private String imgUrl;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id desc")
    private List<FreeBoard> freeBoards = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id desc")
    private List<RecruitBoard> recruitBoards = new ArrayList<>();

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
}
