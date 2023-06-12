package sideeffect.project.repository.freeboard;

import static sideeffect.project.domain.comment.QComment.comment;
import static sideeffect.project.domain.freeboard.QFreeBoard.freeBoard;
import static sideeffect.project.domain.like.QLike.like;
import static sideeffect.project.dto.freeboard.OrderType.COMMENT;
import static sideeffect.project.dto.freeboard.OrderType.LIKE;
import static sideeffect.project.dto.freeboard.OrderType.VIEWS;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;
import sideeffect.project.dto.freeboard.OrderType;
import sideeffect.project.dto.freeboard.RankResponse;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FreeBoardRepositoryImpl implements FreeBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FreeBoardResponse> searchScroll(FreeBoardScrollDto scrollDto, Long userId) {
        Integer filterNumber = getFilterNumber(scrollDto.getLastId(), scrollDto.getOrderType());
        JPAQuery<FreeBoardResponse> queryResult = jpaQueryFactory.select(getResponseConstructor(userId))
            .from(freeBoard)
            .where(filterByOrderType(scrollDto, filterNumber))
            .orderBy(orderByType(scrollDto.getOrderType()), freeBoard.id.desc());
        if (scrollDto.getSize() != null && scrollDto.getSize() > 0) {
            return queryResult.limit(scrollDto.getSize()).fetch();
        }
        return queryResult.fetch();
    }

    @Override
    public List<FreeBoardResponse> searchScrollWithKeyword(FreeBoardScrollDto scrollDto, Long userId) {
        Integer filterNumber = getFilterNumber(scrollDto.getLastId(), scrollDto.getOrderType());
        JPAQuery<FreeBoardResponse> queryResult = jpaQueryFactory.select(getResponseConstructor(userId))
            .from(freeBoard)
            .where(filterByOrderType(scrollDto, filterNumber),
                freeBoard.title.containsIgnoreCase(scrollDto.getKeyword())
                    .or(freeBoard.content.containsIgnoreCase(scrollDto.getKeyword())))
            .orderBy(orderByType(scrollDto.getOrderType()), freeBoard.id.desc());
        if (scrollDto.getSize() != null && scrollDto.getSize() > 0) {
            return queryResult.limit(scrollDto.getSize()).fetch();
        }
        return queryResult.fetch();
    }

    @Override
    public List<RankResponse> searchRankBoard(Integer size, Integer days, Long userId, ChronoUnit chronoUnit) {
        return jpaQueryFactory.select(getRankResponseConstructor(userId))
            .from(freeBoard)
            .leftJoin(freeBoard.likes, like)
            .where(like.createdAt.after(LocalDateTime.now().minus(days, chronoUnit)).or(freeBoard.likes.isNotEmpty()))
            .orderBy(like.count().desc(), freeBoard.likes.size().desc(), freeBoard.views.desc())
            .groupBy(freeBoard.id)
            .limit(size)
            .fetch();
    }

    private BooleanExpression filterByOrderType(FreeBoardScrollDto scrollDto, Integer filterNumber) {
        if (scrollDto.getLastId() == null || scrollDto.getSize() == null) {
            return null;
        }
        OrderType type = scrollDto.getOrderType();
        Long boardId = scrollDto.getLastId();
        if (type.equals(COMMENT)) {
            return freeBoard.comments.size().lt(filterNumber).or(sameNumberFilter(type, filterNumber, boardId));
        } else if (type.equals(LIKE)) {
            return freeBoard.likes.size().lt(filterNumber).or(sameNumberFilter(type, filterNumber, boardId));
        } else if (type.equals(VIEWS)) {
            return freeBoard.views.lt(filterNumber).or(sameNumberFilter(type, filterNumber, boardId));
        }

        return freeBoard.id.lt(boardId);
    }

    private BooleanExpression sameNumberFilter(OrderType type, int size, Long lastId) {
        if (lastId == null) {
            return null;
        }

        if (type.equals(COMMENT)) {
            return freeBoard.comments.size().eq(size).and(freeBoard.id.lt(lastId));
        } else if (type.equals(LIKE)) {
            return freeBoard.likes.size().eq(size).and(freeBoard.id.lt(lastId));
        } else if (type.equals(VIEWS)) {
            return freeBoard.views.eq(size).and(freeBoard.id.lt(lastId));
        }

        return null;
    }

    private Integer getFilterNumber(Long lastId, OrderType orderType) {
        if (lastId == null) {
            return null;
        }

        if (orderType.equals(COMMENT)) {
            return jpaQueryFactory.select(freeBoard.comments.size())
                .from(freeBoard)
                .leftJoin(freeBoard.comments, comment)
                .where(freeBoard.id.eq(lastId))
                .fetchOne();
        } else if (orderType.equals(LIKE)) {
            return jpaQueryFactory.select(freeBoard.likes.size())
                .from(freeBoard)
                .leftJoin(freeBoard.likes, like)
                .where(freeBoard.id.eq(lastId))
                .fetchOne();
        } else if (orderType.equals(VIEWS)) {
            return jpaQueryFactory.select(freeBoard.views)
                .from(freeBoard)
                .where(freeBoard.id.eq(lastId))
                .fetchOne();
        }

        return null;
    }

    private ConstructorExpression<FreeBoardResponse> getResponseConstructor(Long userId) {
        return Projections.constructor(FreeBoardResponse.class,
            freeBoard.id,
            freeBoard.imgUrl,
            freeBoard.subTitle,
            freeBoard.views,
            freeBoard.title,
            freeBoard.createAt,
            getLikeExpression(userId),
            freeBoard.likes.size(),
            freeBoard.comments.size());
    }

    private ConstructorExpression<RankResponse> getRankResponseConstructor(Long userId) {
        return Projections.constructor(RankResponse.class,
            freeBoard.id,
            freeBoard.imgUrl,
            freeBoard.subTitle,
            freeBoard.content,
            freeBoard.views,
            freeBoard.title,
            freeBoard.createAt,
            getLikeExpression(userId),
            freeBoard.likes.size(),
            freeBoard.comments.size());
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

    private OrderSpecifier<?> orderByType(OrderType orderType) {
        Order order = Order.DESC;

        if (orderType.equals(COMMENT)) {
            return new OrderSpecifier<>(order, freeBoard.comments.size());
        } else if (orderType.equals(LIKE)) {
            return new OrderSpecifier<>(order, freeBoard.likes.size());
        } else if (orderType.equals(VIEWS)) {
            return new OrderSpecifier<>(order, freeBoard.views);
        }

        return new OrderSpecifier<>(order, freeBoard.id);
    }
}
