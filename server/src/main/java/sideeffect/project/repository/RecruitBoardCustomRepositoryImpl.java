package sideeffect.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static sideeffect.project.domain.applicant.QApplicant.applicant;
import static sideeffect.project.domain.recruit.QBoardPosition.boardPosition;
import static sideeffect.project.domain.recruit.QRecruitBoard.recruitBoard;

@Slf4j
@RequiredArgsConstructor
public class RecruitBoardCustomRepositoryImpl implements RecruitBoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    @Override
    public List<RecruitBoard> findWithSearchConditions(Long lastId, String keyword, List<StackType> stackTypes, Pageable pageable) {

        String jpql = "select distinct rb from RecruitBoard rb";
        String joinSql = "";
        String whereSql = " where ";
        List<String> whereConditions = new ArrayList<>();

        if(lastId != null) {
            whereConditions.add("rb.id < :lastId");
        }

        if(stackTypes != null && !stackTypes.isEmpty()) {
            joinSql += " join rb.boardStacks bs";
            whereConditions.add("bs.stack.stackType in :stackTypes");
        }

        if(StringUtils.hasText(keyword)) {
            whereConditions.add("(rb.title like concat('%',:keyword,'%') or rb.contents like concat('%',:keyword,'%'))");
        }

        if(StringUtils.hasText(joinSql)) {
            jpql += joinSql;
        }

        if(!whereConditions.isEmpty()) {
            jpql += whereSql;
            jpql += String.join(" and ", whereConditions);
        }

        jpql += " order by rb.id desc";

        TypedQuery<RecruitBoard> query = em.createQuery(jpql, RecruitBoard.class);

        if(lastId != null) {
            query.setParameter("lastId", lastId);
        }

        if(stackTypes != null && !stackTypes.isEmpty()) {
            query.setParameter("stackTypes", stackTypes);
        }

        if(StringUtils.hasText(keyword)) {
            query.setParameter("keyword", keyword);
        }

        return query.setMaxResults(pageable.getPageSize()).getResultList();
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
