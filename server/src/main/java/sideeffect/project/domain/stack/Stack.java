package sideeffect.project.domain.stack;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stack_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "stack_name", unique = true)
    private StackType stackType;

    private String url;

    @Builder
    public Stack(StackType stackType, String url) {
        this.stackType = stackType;
        this.url = url;
    }
}
