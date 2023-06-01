package sideeffect.project.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.redis.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenProviderTest {

    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private static final String EMAIL = "test@naver.com";

    @BeforeEach
    void setUp() {
        refreshTokenProvider = new RefreshTokenProvider(refreshTokenRepository, jwtTokenProvider);
    }

    @DisplayName("엑세스 토큰을 재발급 한다.")
    @Test
    void issueAccessToken() {
        String token = UUID.randomUUID().toString();
        Long userId = 1L;
        RefreshToken refreshToken = new RefreshToken(token, userId);
        when(refreshTokenRepository.findById(any())).thenReturn(Optional.of(refreshToken));

        refreshTokenProvider.issueAccessToken(token);

        assertAll(
            () -> verify(refreshTokenRepository).findById(any()),
            () -> verify(jwtTokenProvider).createAccessToken(anyLong())
        );
    }

    @DisplayName("refresh token을 발급한다.")
    @Test
    void createRefreshToken() {
        Authentication authentication = createAuthentication();
        refreshTokenProvider.createRefreshToken(authentication);

        verify(refreshTokenRepository).save(any());
    }

    @DisplayName("refresh token을 삭제한다.")
    @Test
    void deleteToken() {
        String token = UUID.randomUUID().toString();

        refreshTokenProvider.deleteToken(token);

        verify(refreshTokenRepository).deleteById(any());
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
