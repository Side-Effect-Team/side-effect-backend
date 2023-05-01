package sideeffect.project.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import javax.persistence.EntityManager;


@DataJpaTest
class UserRepositoryTest {

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