package sideeffect.project.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.config.security.AuthProperties;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthProperties authProperties;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private User user;
    private String secretKey;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(authProperties, userDetailsService);

        secretKey = UUID.randomUUID().toString();

        user = User.builder()
            .email("test@naver.com")
            .password("123455")
            .providerType(ProviderType.DEFAULT)
            .userRoleType(UserRoleType.ROLE_USER)
            .build();
    }

    @DisplayName("엑세스 토큰을 발급한다.")
    @Test
    void createAccessToken() {
        when(userDetailsService.loadUserByUserId(any())).thenReturn(UserDetailsImpl.of(user));
        when(authProperties.getSecret()).thenReturn(secretKey);
        String accessToken = tokenProvider.createAccessToken(user.getId());

        System.out.println("accessToken = " + accessToken);
        assertAll(
            () -> assertThat(accessToken).isNotNull(),
            () -> verify(userDetailsService).loadUserByUserId(any()),
            () -> verify(authProperties).getSecret()
        );
    }

    @DisplayName("엑세스 토큰으로 인증 객체를 추출한다.")
    @Test
    void getAuthentication() {
        Long time = 1000 * 60L;
        String token = createToken(time);
        when(authProperties.getSecret()).thenReturn(secretKey);
        when(userDetailsService.loadUserByUsernameAndProviderType(any(), any()))
            .thenReturn(UserDetailsImpl.of(user));

        tokenProvider.getAuthentication(token);

        assertAll(
            () -> verify(userDetailsService).loadUserByUsernameAndProviderType(any(), any()),
            () -> verify(authProperties).getSecret()
        );
    }

    @DisplayName("만료된 토큰을 받으면 예외가 발생한다.")
    @Test
    void inputExpiredAccessToken() {
        String token = createToken(0L);
        when(authProperties.getSecret()).thenReturn(secretKey);

        assertThatThrownBy(() -> tokenProvider.validateAccessToken(token))
            .isInstanceOf(AuthException.class)
            .hasMessage(ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
    }

    @DisplayName("토큰이 null이면 예외가 발생된다.")
    @Test
    void inputNullAccessToken() {
        String token = null;
        when(authProperties.getSecret()).thenReturn(secretKey);

        assertThatThrownBy(() -> tokenProvider.validateAccessToken(token))
            .isInstanceOf(AuthException.class)
            .hasMessage(ErrorCode.ACCESS_TOKEN_ILLEGAL_STATE.getMessage());
    }

    @DisplayName("토큰이 잘못된 형식이면 예외가 발생한다.")
    @Test
    void inputMalformedAccessToken() {
        String token = "hello";
        when(authProperties.getSecret()).thenReturn(secretKey);

        assertThatThrownBy(() -> tokenProvider.validateAccessToken(token))
            .isInstanceOf(AuthException.class)
            .hasMessage(ErrorCode.ACCESS_TOKEN_MALFORMED.getMessage());
    }

    @DisplayName("다른 키로 암호화된 토큰이면 예외가 발생한다.")
    @Test
    void inputOtherSignatureAccessToken() {
        String otherKey = "1234";
        String token = Jwts.builder()
            .setSubject(user.getEmail())
            .claim("auth", user.getUserRoleType())
            .claim("providerType", user.getProviderType())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
            .signWith(SignatureAlgorithm.HS256, otherKey)
            .compact();
        when(authProperties.getSecret()).thenReturn(secretKey);

        assertThatThrownBy(() -> tokenProvider.validateAccessToken(token))
            .isInstanceOf(AuthException.class)
            .hasMessage(ErrorCode.ACCESS_TOKEN_SIGNATURE_FAILED.getMessage());
    }


    private String createToken(Long time) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("auth", user.getUserRoleType())
            .claim("providerType", user.getProviderType())
            .setExpiration(new Date(System.currentTimeMillis() + time))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}
