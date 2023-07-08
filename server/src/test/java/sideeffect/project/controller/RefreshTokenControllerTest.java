package sideeffect.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import sideeffect.project.common.docs.ControllerTestDocument;
import sideeffect.project.common.docs.auth.AuthDocsUtils;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.security.RefreshTokenProvider;

@WebMvcTest(RefreshTokenController.class)
class RefreshTokenControllerTest extends ControllerTestDocument {

    private static final Long EXPIRE_TIME = 1000L;
    private static final String SECRET = "secret";

    @MockBean
    private RefreshTokenProvider refreshTokenProvider;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .providerType(ProviderType.GOOGLE)
            .userRoleType(UserRoleType.ROLE_USER)
            .email("test@naver.com")
            .password("1234")
            .build();
    }

    @WithCustomUser
    @DisplayName("토큰을 재발급 한다.")
    @Test
    void issue() throws Exception {
        Cookie cookie = createCookie(UUID.randomUUID().toString());
        given(refreshTokenProvider.issueAccessToken(any())).willReturn(generateAccessToken());

        mvc.perform(RestDocumentationRequestBuilders.post("/api/token/at-issue")
                .cookie(cookie)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("auth/at-issue", AuthDocsUtils.getRefreshTokenDocs()));
    }

    @DisplayName("로그아웃을 하면 빈 쿠키를 받는다")
    @WithCustomUser
    @Test
    void logout() throws Exception {
        Cookie cookie = createCookie(UUID.randomUUID().toString());

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/token/at-issue")
                .cookie(cookie)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("auth/logout", AuthDocsUtils.getLogoutTokenDocs()));
    }

    private String generateAccessToken() {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("auth", UserRoleType.ROLE_USER)
            .claim("providerType", user.getProviderType())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }

    private Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24 * 3);
        cookie.setPath("/api/token/at-issue");
        return cookie;
    }
}
