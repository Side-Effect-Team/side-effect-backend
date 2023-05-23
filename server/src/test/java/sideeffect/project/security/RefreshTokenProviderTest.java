package sideeffect.project.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.redis.RefreshTokenRepository;
import sideeffect.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenProviderTest {

    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    private static final String EMAIL = "test@naver.com";

    @BeforeEach
    void setUp() {
        refreshTokenProvider = new RefreshTokenProvider(refreshTokenRepository, userRepository);
        ReflectionTestUtils.setField(refreshTokenProvider, "secretKey", "testKey");
    }

    @Test
    void issueAccessToken() {
        String token = UUID.randomUUID().toString();
        Long userId = 1L;
        RefreshToken refreshToken = new RefreshToken(token, userId);
        when(refreshTokenRepository.findById(any())).thenReturn(Optional.of(refreshToken));
        when(userRepository.findEmailByUserId(any())).thenReturn(Optional.of(EMAIL));

        refreshTokenProvider.issueAccessToken(token);

        assertAll(
            () -> verify(refreshTokenRepository).findById(any()),
            () -> verify(userRepository).findEmailByUserId(any())
        );
    }

    @Test
    void createRefreshToken() {
        Authentication authentication = createAuthentication();
        refreshTokenProvider.createRefreshToken(authentication);

        verify(refreshTokenRepository).save(any());
    }

    private Authentication createAuthentication() {
        User user = User.builder().email(EMAIL)
            .password("12345")
            .userRoleType(UserRoleType.ROLE_USER)
            .build();
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);
        return new UsernamePasswordAuthenticationToken(userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities());
    }
}
