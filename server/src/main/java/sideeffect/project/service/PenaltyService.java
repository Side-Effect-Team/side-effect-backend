package sideeffect.project.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.penalty.Penalty;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.repository.PenaltyRepository;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    public void penalize(User user, Applicant applicant) {
        RecruitBoard recruitBoard = applicant.getBoardPosition().getRecruitBoard();
        penaltyRepository.save(Penalty.penalize(user, recruitBoard));
    }

    public boolean isPenalized(User user, RecruitBoard recruitBoard) {
        Optional<Penalty> penalty = penaltyRepository.findByUserIdAndRecruitBoardId(user.getId(),
            recruitBoard.getId());
        return penalty.filter(this::validatePenalty).isPresent();
    }

    private boolean validatePenalty(Penalty penalty) {
        if (LocalDateTime.now().isAfter(penalty.getCreatedAt().plusDays(1))) {
            penaltyRepository.delete(penalty);
            return false;
        }
        return true;
    }
}
