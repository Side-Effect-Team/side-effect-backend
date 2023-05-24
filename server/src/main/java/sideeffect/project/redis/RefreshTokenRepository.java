package sideeffect.project.redis;

import org.springframework.data.repository.CrudRepository;
import sideeffect.project.domain.token.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    boolean existsByRefreshToken(String refreshToken);
}
