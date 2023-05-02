package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.repository.PositionRepository;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    @Transactional(readOnly = true)
    public Position findByPositionType(PositionType positionType) {
        return positionRepository.findByPositionType(positionType)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POSITION_NOT_FOUND));
    }

}
