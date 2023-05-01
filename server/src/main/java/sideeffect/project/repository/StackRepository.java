package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;

import java.util.Optional;

public interface StackRepository extends JpaRepository<Stack, Long> {

    Optional<Stack> findByStackType(StackType stackType);
}
