package sideeffect.project.repository;

import org.springframework.data.repository.CrudRepository;
import sideeffect.project.domain.token.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
