package sideeffect.project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.repository.PositionRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @InjectMocks
    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    @DisplayName("포지션을 조회한다.")
    @Test
    void findByPositionType() {
        Position position = Position.builder()
                .positionType(PositionType.BACKEND)
                .build();

        when(positionRepository.findByPositionType(any())).thenReturn(Optional.of(position));

        positionService.findByPositionType(PositionType.BACKEND);

        verify(positionRepository).findByPositionType(any());
    }
}