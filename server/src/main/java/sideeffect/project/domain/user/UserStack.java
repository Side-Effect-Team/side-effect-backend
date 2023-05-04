package sideeffect.project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;

import javax.persistence.*;

@Entity
@Table(name = "USER_STACK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStack {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StackLevelType stackLevelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserStack(Long id, StackLevelType stackLevelType, Stack stack, User user){
        this.id = id;
        this.stackLevelType = stackLevelType;
        this.stack = stack;
        this.user = user;
    }
}
