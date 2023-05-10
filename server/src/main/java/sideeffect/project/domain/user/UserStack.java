package sideeffect.project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.stack.Stack;

import javax.persistence.*;

@Entity
@Table(name = "USER_STACK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStack {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserStack(Long id, Stack stack, User user){
        this.id = id;
        this.stack = stack;
        this.user = user;
    }
}
