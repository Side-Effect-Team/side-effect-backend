package sideeffect.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(user).isEqualTo(finduser);
    }

    @DisplayName("유저 id로 이메일 조회")
    @Test
    void findEmailByUserId() {
        String email = "google@google.com";
        User user = User.builder()
            .email(email)
            .build();
        userRepository.save(user);

        String result = userRepository.findEmailByUserId(user.getId()).orElse(null);

        assertThat(result).isEqualTo(email);
    }
}
