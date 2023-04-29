package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.UserJoinRequest;
import sideeffect.project.repository.UserRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;

    BCryptPasswordEncoder encoder;

    @Mock
    UserRepository userRepository;

    User user;

    @BeforeEach
    void beforeEach(){
        encoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, encoder);
        user = User.builder()
                .id(1L)
                .email("google@google.com")
                .password(encoder.encode("1234"))
                .nickname("ABC")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();
    }

    @DisplayName("회원가입")
    @Test
    void join(){
        UserJoinRequest request = UserJoinRequest.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .build();

        when(userRepository.save(any())).thenReturn(user);

        userService.join(request);

        verify(userRepository).save(any());
    }

}