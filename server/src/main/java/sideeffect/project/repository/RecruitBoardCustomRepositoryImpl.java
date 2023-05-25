package sideeffect.project.repository;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.dto.recruit.RecruitBoardAndLikeDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static sideeffect.project.domain.applicant.QApplicant.applicant;
import static sideeffect.project.domain.like.QRecruitLike.recruitLike;
import static sideeffect.project.domain.recruit.QBoardPosition.boardPosition;
import static sideeffect.project.domain.recruit.QBoardStack.boardStack;
import static sideeffect.project.domain.recruit.QRecruitBoard.recruitBoard;
import static sideeffect.project.domain.stack.QStack.stack;

@Slf4j
@RequiredArgsConstructor
public class RecruitBoardCustomRepositoryImpl implements RecruitBoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    @Override
    public List<RecruitBoardAndLikeDto> findWithSearchConditions(Long userId, Long lastId, String keyword, List<StackType> stackTypes, Integer size) {
        JPAQuery<RecruitBoardAndLikeDto> query = jpaQueryFactory.selectDistinct(getResponseConstructor(userId)).from(recruitBoard);

        if(stackTypes != null && !stackTypes.isEmpty()) {
            query.innerJoin(recruitBoard.boardStacks, boardStack).innerJoin(boardStack.stack, stack);
        }

        return query.where(lastIdLt(lastId), addKeywordCondition(keyword), addStackTypeCondition(stackTypes))
                .orderBy(recruitBoard.id.desc())
                .limit(size).fetch();
    }

    @Override
    public Optional<RecruitBoardAndLikeDto> findByBoardIdAndUserId(Long boardId, Long userId) {
        RecruitBoardAndLikeDto recruitBoardAndLikeDto = jpaQueryFactory.select(getResponseConstructor(userId))
                .from(recruitBoard)
                .where(recruitBoard.id.eq(boardId))
                .fetchOne();

        return Optional.ofNullable(recruitBoardAndLikeDto);
    }

    @Override
    public List<RecruitBoardAndLikeDto> findByAllWithLike(Long userId) {
        return jpaQueryFactory.select(getResponseConstructor(userId))
                .from(recruitBoard)
                .fetch();
    }

    private ConstructorExpression<RecruitBoardAndLikeDto> getResponseConstructor(Long userId) {

        return Projections.constructor(RecruitBoardAndLikeDto.class,
                recruitBoard,
                getLikeExpression(userId)
        );
    }
    private Expression<Boolean> getLikeExpression(Long userId) {
        if (userId == null) {
            return Expressions.asBoolean(false).isTrue();
        }
        return ExpressionUtils.as(JPAExpressions.selectOne()
                        .from(recruitLike)
                        .where(recruitLike.user.id.eq(userId).and(recruitLike.recruitBoard.id.eq(recruitBoard.id))).limit(1).isNotNull(),
                "like");
    }

    private BooleanExpression lastIdLt(Long lastId) {
        return lastId != null ? recruitBoard.id.lt(lastId) : null;
    }

    private BooleanExpression addKeywordCondition(String keyword) {
        return hasText(keyword) ? recruitBoard.title.containsIgnoreCase(keyword).or(recruitBoard.contents.containsIgnoreCase(keyword)) : null;
    }

    private BooleanExpression addStackTypeCondition(List<StackType> stackTypes) {
        if(stackTypes == null || stackTypes.isEmpty()) {
            return null;
        }

        return boardStack.stack.stackType.in(stackTypes);
    }

    @Override
    public boolean existsApplicantByRecruitBoard(Long boardId, Long userId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(recruitBoard)
                .innerJoin(recruitBoard.boardPositions, boardPosition)
                .innerJoin(boardPosition.applicants, applicant)
                .where(recruitBoard.id.eq(boardId), applicant.user.id.eq(userId))
                .fetchFirst();

        return fetchOne != null;
    }
}
