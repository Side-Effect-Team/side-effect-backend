package sideeffect.project.common.docs.freeBoard;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippet;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

public final class FreeBoardLikeDocsUtils {

    public static ResourceSnippet getFreeBoardLikeToggleDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .description("자랑 게시판 좋아요를 토글한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .pathParameters(parameterWithName("id").description("자랑게시판 id"))
                .responseFields(
                    fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시판 아이디"),
                    fieldWithPath("userNickname").type(JsonFieldType.STRING).description("좋아요 유저 닉네임"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                )
                .build());
    }
}
