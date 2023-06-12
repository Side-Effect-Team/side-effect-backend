package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sideeffect.project.common.fileupload.service.UserUploadService;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.UserEditResponse;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder encoder;

    @Mock
    UserUploadService userUploadService;

    User user;
    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .id(1L)
                .email("google@google.com")
                .password(encoder.encode("1234"))
                .nickname("ABC")
                .introduction("안녕하세요")
                .position(PositionType.BACKEND)
                .career("junior")
                .providerType(ProviderType.GOOGLE)
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();
    }

    @DisplayName("회원가입")
    @Test
    void join(){
        UserRequest request = UserRequest.builder()
                .email("google@google.com")
                .password("1234")
                .nickname("ABC")
                .introduction("안녕하세요")
                .position(PositionType.BACKEND)
                .career("junior")
                .tags(List.of("AAA", "BBB"))
                .providerType(ProviderType.GOOGLE)
                .blogUrl("tistory/tlsrl6427")
                .githubUrl("github/tlsrl6427")
                .portfolioUrl("naver.com/tlsrl6427")
                .build();

        when(userRepository.save(any())).thenReturn(user);

        userService.join(request);

       verify(userRepository).save(any());
    }

    @DisplayName("단일 회원 조회(마이페이지일 경우)")
    @Test
    void findOne(){
        UserResponse userResponse = userService.findOne(user, 1L);
        assertAll(
            ()->assertThat(userResponse.getEmail()).isEqualTo(user.getEmail()),
            ()->assertThat(userResponse.getNickname()).isEqualTo(user.getNickname()),
            ()->assertThat(userResponse.getIsOwner()).isEqualTo(true)
        );
    }

    @DisplayName("단일 회원 조회(다른 유저의 프로필일 경우)")
    @Test
    void findOneWithOtherUser(){
        User findUser = User.builder()
                .id(2L)
                .email("test@gmail.com")
                .nickname("test")
                .build();

        //when(userRepository.findById(any())).thenReturn(Optional.of(findUser));
        doReturn(Optional.of(findUser)).when(userRepository).findById(any());
        UserResponse userResponse = userService.findOne(user, 2L);

        assertAll(
                ()->verify(userRepository).findById(any()),
                ()->assertThat(userResponse.getEmail()).isEqualTo(findUser.getEmail()),
                ()->assertThat(userResponse.getNickname()).isEqualTo(findUser.getNickname()),
                ()->assertThat(userResponse.getIsOwner()).isEqualTo(false)
        );
    }

    @DisplayName("수정페이지 유저정보조회")
    @Test
    void findEditInfo() {
        UserEditResponse  userEditResponse = userService.findEditInfo(user);
        assertAll(
                () -> assertThat(userEditResponse.getNickname()).isEqualTo(user.getNickname()),
                () -> assertThat(userEditResponse.getIntroduction()).isEqualTo(user.getIntroduction()),
                () -> assertThat(userEditResponse.getPosition()).isEqualTo(user.getPosition()),
                () -> assertThat(userEditResponse.getCareer()).isEqualTo(user.getCareer()),
                () -> assertThat(userEditResponse.getBlogUrl()).isEqualTo(user.getBlogUrl()),
                () -> assertThat(userEditResponse.getGithubUrl()).isEqualTo(user.getGithubUrl()),
                () -> assertThat(userEditResponse.getPortfolioUrl()).isEqualTo(user.getPortfolioUrl())
        );
    }

    @DisplayName("회원 업데이트")
    @Test
    void update(){
        UserRequest request = UserRequest.builder()
                .nickname("updatedNickname")
                .introduction("test")
                .position(PositionType.FRONTEND)
                .career("senior")
                .tags(List.of("AAA", "BBB"))
                .build();

        userService.update(user, 1L, request);

        assertAll(
            () -> assertThat(user.getNickname()).isEqualTo(request.getNickname()),
            () -> assertThat(user.getIntroduction()).isEqualTo(request.getIntroduction()),
            () -> assertThat(user.getPosition()).isEqualTo(request.getPosition()),
            () -> assertThat(user.getCareer()).isEqualTo(request.getCareer()),
            () -> assertThat(user.getUserStacks()).hasSize(2)
        );

    }

    @DisplayName("회원 삭제")
    @Test
    void delete(){

        userService.delete(user, 1L);

        verify(userRepository).deleteById(any());
    }

    @DisplayName("닉네임 중복여부 체크")
    @Test
    void duplicateNickname(){
        when(userRepository.findByNickname(any())).thenReturn(Optional.of(user));
        Boolean isDuplicated = userService.duplicateNickname(user.getNickname());

        assertAll(
                () -> verify(userRepository).findByNickname(any()),
                () -> assertThat(isDuplicated).isEqualTo(true)
        );
    }

    @DisplayName("이미지 등록")
    @Test
    void uploadImage() throws IOException {
        String filepath = "/test.jpg";
        doReturn(filepath).when(userUploadService).storeFile(any());

        userService.uploadImage(user, new MockMultipartFile("test", "test content".getBytes()));

        assertAll(
                () -> verify(userUploadService).storeFile(any()),
                () -> assertThat(user.getImgUrl()).isEqualTo(filepath)
        );
    }

    @DisplayName("이미지 경로 받아오기")
    @Test
    void getImageFullPath(){
        String image = "test.jpg";
        String actual_path = "/test.jpg";

        doReturn(actual_path).when(userUploadService).getFullPath(any());

        String findpath = userUploadService.getFullPath(image);

        assertAll(
                () -> verify(userUploadService).getFullPath(any()),
                () -> assertThat(findpath).isEqualTo(actual_path)
        );
    }

}
