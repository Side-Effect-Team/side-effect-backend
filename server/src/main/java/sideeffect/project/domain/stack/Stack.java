package sideeffect.project.domain.stack;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(
        name = "STACKS",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "stack_name"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stack_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "stack_name")
    private StackType stackType;

    private String url;

    @Builder
    public Stack(Long id, StackType stackType, String url) {
        this.id = id;
        this.stackType = stackType;
        this.url = url;
    }
}
