package sideeffect.project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "USER_STACK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStack {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stack_name")
    private String stack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserStack(Long id, String stack, User user){
        this.id = id;
        this.stack = stack;
        this.user = user;
    }
}
