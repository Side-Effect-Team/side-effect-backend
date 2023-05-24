package sideeffect.project.repository.user;

import static sideeffect.project.domain.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<String> findEmailByUserId(Long userId) {
        String email = findEmail(userId);
        return Optional.ofNullable(email);
    }

    private String findEmail(Long userId) {
        return jpaQueryFactory.select(user.email)
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne();
    }
}
