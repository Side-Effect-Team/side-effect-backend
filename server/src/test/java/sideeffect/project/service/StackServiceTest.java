package sideeffect.project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.repository.StackRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StackServiceTest {

    @InjectMocks
    private StackService stackService;

    @Mock
    private StackRepository stackRepository;

    @DisplayName("기술 스택을 조회한다.")
    @Test
    void findByStackType() {
        Stack stack = Stack.builder()
                .stackType(StackType.SPRING)
                .build();

        when(stackRepository.findByStackType(any())).thenReturn(Optional.of(stack));

        stackService.findByStackType(StackType.SPRING);

        verify(stackRepository).findByStackType(any());
    }
}