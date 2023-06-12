package sideeffect.project.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sideeffect.project.common.jpa.TestDataRepository;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;

import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("nickname으로 User 조회")
    @Test
    void findByNickname() {
        User user = User.builder()
            .nickname("test")
            .build();
        userRepository.save(user);

        User finduser = userRepository.findByNickname("test").orElse(null);

        assertThat(user).isEqualTo(finduser);
    }

    @DisplayName("email, providerType으로 User 조회")
    @Test
    void findByEmailAndProvider() {
        User user = User.builder()
                .email("test@gmail.com")
                .providerType(ProviderType.GOOGLE)
                .build();

        userRepository.save(user);

        User findUser = userRepository.findByEmailAndProvider("test@gmail.com", ProviderType.GOOGLE).orElse(null);
        assertThat(user).isEqualTo(findUser);
    }
}
