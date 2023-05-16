package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackLevelType;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.UserPositionRequest;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.dto.user.UserStackRequest;
import sideeffect.project.repository.UserPositionRepository;
import sideeffect.project.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PositionService positionService;

    @Mock
    StackService stackService;

    @Mock
    UserPositionRepository userPositionRepository;

    BCryptPasswordEncoder encoder;

    User user;
    Position postion;
    Stack stack;
    @BeforeEach
    void beforeEach(){
        encoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, positionService, stackService, encoder);
        user = User.builder()
                .id(1L)
                .email("google@google.com")
                .password(encoder.encode("1234"))
                .nickname("ABC")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();

        postion = Position.builder()
                .id(1L)
                .positionType(PositionType.BACKEND)
                .build();

        stack = Stack.builder()
                .id(1L)
                .stackType(StackType.JAVA)
                .build();

    }

    @DisplayName("회원가입")
    @Test
    void join(){
        UserRequest request = UserRequest.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .positions(List.of(
                        new UserPositionRequest(PositionType.BACKEND, "1")
                ))
                .stacks(List.of(
                        new UserStackRequest(StackType.JAVA, StackLevelType.HIGH)
                ))
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .build();

        when(userRepository.save(any())).thenReturn(user);
        when(positionService.findByPositionType(any())).thenReturn(postion);
        when(stackService.findByStackType(any())).thenReturn(stack);

        userService.join(request);

        verify(userRepository).save(any());
    }

    @DisplayName("단일 회원 조회")
    @Test
    void findOne(){
        UserResponse userResponse = userService.findOne(user, 1L);
        assertAll(
            ()->assertThat(userResponse.getEmail()).isEqualTo(user.getEmail()),
            ()->assertThat(userResponse.getNickname()).isEqualTo(user.getNickname()),
            ()->assertThat(userResponse.getUserRoleType()).isEqualTo(user.getUserRoleType())
        );
    }

    @DisplayName("회원 업데이트")
    @Test
    void update(){
        UserRequest request = UserRequest.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .build();

        userService.update(user, 1L, request);

        assertAll(
            () -> assertThat(user.getEmail()).isEqualTo(request.getEmail()),
            () -> assertThat(encoder.matches(request.getPassword(), user.getPassword())).isTrue(),
            () -> assertThat(user.getNickname()).isEqualTo(request.getNickname())
        );

    }

    @DisplayName("회원 삭제")
    @Test
    void delete(){

        userService.delete(user, 1L);

        verify(userRepository).deleteById(any());
    }

}