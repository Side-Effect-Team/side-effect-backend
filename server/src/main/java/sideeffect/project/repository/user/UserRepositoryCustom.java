package sideeffect.project.repository.user;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<String> findEmailByUserId(Long userId);
}
