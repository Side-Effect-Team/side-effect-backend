package sideeffect.project.common.docs.auth;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippet;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

public final class AuthDocsUtils {

    public static ResourceSnippet getOauthLoginDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("로그인 API")
                .description("소셜 로그인을 진행한다.")
                .requestHeaders(
                    headerWithName("providerType").description("소셜 로그인 provider"),
                    headerWithName("token").description("소셜 로그인 엑세스 토근"))
                .responseHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰"),
                    headerWithName(HttpHeaders.SET_COOKIE).description("리프레시 토큰")
                )
                .responseFields(fieldWithPath("userId").type(JsonFieldType.NUMBER).description("로그인 유저 아이디"))
                .build());
    }

    public static ResourceSnippet getRefreshTokenDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("로그인 API")
                .description("토큰을 재발급한다.")
                .requestHeaders(
                    headerWithName("Cookie").description("refresh token 쿠키").optional())
                .responseHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰"))
                .build());
    }

    public static ResourceSnippet getLogoutTokenDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("로그인 API")
                .description("로그아웃 한다.")
                .requestHeaders(
                    headerWithName("Cookie").description("refresh token 쿠키").optional())
                .responseHeaders(headerWithName(HttpHeaders.SET_COOKIE).description("빈 쿠키"))
                .build());
    }
}
