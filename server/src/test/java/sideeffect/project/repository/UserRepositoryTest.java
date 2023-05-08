package sideeffect.project.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.user.User;

class UserRepositoryTest extends TestDataRepository {

    @Autowired
    UserRepository userRepository;

    @DisplayName("email로 User 조회")
    @Test
    void findByEmail(){
        User user = User.builder()
                .email("google@google.com")
                .build();

        userRepository.save(user);

        User finduser = userRepository.findByEmail("google@google.com").orElse(null);

        Assertions.assertThat(user).isEqualTo(finduser);
    }
}
