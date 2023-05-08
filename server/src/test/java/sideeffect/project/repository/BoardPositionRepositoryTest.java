package sideeffect.project.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class BoardPositionRepositoryTest {

    @Autowired
    BoardPositionRepository boardPositionRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ApplicantRepository applicantRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    private Position backEndPosition;
    private User user;

    @BeforeEach
    void setUp() {
        backEndPosition = Position.builder().positionType(PositionType.BACKEND).build();
        positionRepository.save(backEndPosition);

        user = User.builder()
                .nickname("tester")
                .password("1234")
                .userRoleType(UserRoleType.ROLE_USER)
                .email("test@naver.com")
                .build();

        userRepository.save(user);

        em.clear();
    }

    @DisplayName("게시판 특정 포지션에 팀원을 모집할 수 있다면 포지션을 반환한다.")
    @Test
    void findBoardPositionIfRecruitable() {
        BoardPosition boardPosition = BoardPosition.builder().position(backEndPosition).targetNumber(1).build();
        BoardPosition savedBoardPosition = boardPositionRepository.save(boardPosition);

        Applicant applicant = Applicant.builder().build();
        applicant.associate(user, savedBoardPosition);
        Applicant savedApplicant = applicantRepository.save(applicant);

        boolean present = boardPositionRepository.findBoardPositionIfRecruitable(savedApplicant.getId()).isPresent();

        assertThat(present).isTrue();
    }

    @DisplayName("게시판 특정 포지션에 팀원을 모집할 수 없다면 빈 객체를 반환한다.")
    @Test
    void findBoardPositionIfNotRecruitable() {
        BoardPosition boardPosition = BoardPosition.builder().position(backEndPosition).targetNumber(0).build();
        BoardPosition savedBoardPosition = boardPositionRepository.save(boardPosition);

        Applicant applicant = Applicant.builder().build();
        applicant.associate(user, savedBoardPosition);
        Applicant savedApplicant = applicantRepository.save(applicant);

        boolean present = boardPositionRepository.findBoardPositionIfRecruitable(savedApplicant.getId()).isPresent();

        assertThat(present).isFalse();
    }

    @DisplayName("지원자가 지원한 포지션을 반환한다.")
    @Test
    void findByApplicantId() {
        BoardPosition boardPosition = BoardPosition.builder().position(backEndPosition).targetNumber(1).build();
        BoardPosition savedBoardPosition = boardPositionRepository.save(boardPosition);

        Applicant applicant = Applicant.builder().build();
        applicant.associate(user, savedBoardPosition);
        Applicant savedApplicant = applicantRepository.save(applicant);

        BoardPosition findBoardPosition = boardPositionRepository.findByApplicantId(savedApplicant.getId()).orElse(null);

        assertAll(
                () -> assertThat(findBoardPosition).isNotNull(),
                () -> assertThat(findBoardPosition.getPosition()).isEqualTo(backEndPosition),
                () -> assertThat(findBoardPosition.getApplicants()).containsExactly(savedApplicant)
        );

    }

}