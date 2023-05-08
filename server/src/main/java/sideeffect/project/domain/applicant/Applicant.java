package sideeffect.project.domain.applicant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.common.domain.BaseTimeEntity;
import sideeffect.project.domain.recruit.BoardPosition;
import sideeffect.project.domain.user.User;

import javax.persistence.*;

@Entity
@Table(name = "APPLICANT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicantStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_position_id")
    private BoardPosition boardPosition;

    @Builder
    public Applicant(Long id) {
        this.id = id;
        this.status = ApplicantStatus.PENDING;
    }

    public void associate(User user, BoardPosition boardPosition) {
        boardPosition.addApplicant(this);
        user.addApplicant(this);
        this.user = user;
        this.boardPosition = boardPosition;
    }

    public void updateStatus(ApplicantStatus status) {
        this.status = status;
    }

}
