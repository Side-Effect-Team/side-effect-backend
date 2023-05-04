package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.repository.StackRepository;

@Service
@RequiredArgsConstructor
public class StackService {

    private final StackRepository stackRepository;

    @Transactional(readOnly = true)
    public Stack findByStackType(StackType stackType) {
        return stackRepository.findByStackType(stackType)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STACK_NOT_FOUND));
    }

}
