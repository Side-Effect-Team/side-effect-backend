package sideeffect.project.repository.freeboard;

import static sideeffect.project.domain.freeboard.QFreeBoard.freeBoard;
import static sideeffect.project.domain.like.QLike.like;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sideeffect.project.dto.freeboard.FreeBoardResponse;


@Repository
@RequiredArgsConstructor
public class FreeBoardRepositoryImpl implements FreeBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FreeBoardResponse> searchScroll(Long lastId, Long userId, Integer size) {
        return jpaQueryFactory.select(getResponseConstructor(userId))
            .from(freeBoard)
            .where(boardIdLt(lastId))
            .orderBy(freeBoard.id.desc())
            .limit(size)
            .fetch();
    }

    @Override
    public List<FreeBoardResponse> searchScrollWithKeyword(Long lastId, Long userId, String keyword, Integer size) {
        return jpaQueryFactory.select(getResponseConstructor(userId))
            .from(freeBoard)
            .where(boardIdLt(lastId),
                freeBoard.content.containsIgnoreCase(keyword).or(freeBoard.title.containsIgnoreCase(keyword)))
            .orderBy(freeBoard.id.desc())
            .limit(size)
            .fetch();
    }

    private ConstructorExpression<FreeBoardResponse> getResponseConstructor(Long userId) {
        return Projections.constructor(FreeBoardResponse.class,
            freeBoard.id,
            freeBoard.views,
            freeBoard.user.nickname,
            freeBoard.title,
            freeBoard.content,
            freeBoard.imgUrl,
            freeBoard.likes.size(),
            freeBoard.comments.size(),
            getLikeExpression(userId),
            freeBoard.createAt);
    }


    private BooleanExpression boardIdLt(Long boardId) {
        if (boardId != null) {
            return freeBoard.id.lt(boardId);
        }
        return null;
    }

    private Expression<Boolean> getLikeExpression(Long userId) {
        if (userId == null) {
            return Expressions.asBoolean(false).isTrue();
        }
        return ExpressionUtils.as(JPAExpressions.selectOne()
                .from(like)
                .where(like.user.id.eq(userId).and(like.freeBoard.id.eq(freeBoard.id))).limit(1).isNotNull(),
            "like");

    }
}
