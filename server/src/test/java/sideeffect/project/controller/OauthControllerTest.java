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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import sideeffect.project.common.docs.ControllerTestDocument;
import sideeffect.project.common.docs.auth.AuthDocsUtils;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.security.RefreshTokenProvider;
import sideeffect.project.service.OauthService;

@WebMvcTest(OauthController.class)
class OauthControllerTest extends ControllerTestDocument {

    private static final Long EXPIRE_TIME = 1000L;
    private static final String SECRET = "secret";

    @MockBean
    private OauthService oauthService;

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

    @DisplayName("providerType, token으로 소셜로그인")
    @WithCustomUser
    @Test
    void login() throws Exception {
        String providerType = "google";
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
            .refreshToken(UUID.randomUUID().toString())
            .userId(user.getId())
            .build();

        given(oauthService.login(any(), any())).willReturn(user);
        given(refreshTokenProvider.createRefreshToken(any())).willReturn(refreshToken);
        given(refreshTokenProvider.issueAccessToken(any())).willReturn(generateAccessToken());

        mvc.perform(RestDocumentationRequestBuilders.post("/api/social/login")
                .header("providerType", providerType)
                .header("token", token)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("auth/social-login", AuthDocsUtils.getOauthLoginDocs()));
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
}
