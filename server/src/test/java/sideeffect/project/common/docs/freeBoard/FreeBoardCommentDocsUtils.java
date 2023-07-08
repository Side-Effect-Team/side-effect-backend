package sideeffect.project.common.docs.freeBoard;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippet;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.http.HttpHeaders;

public final class FreeBoardCommentDocsUtils {

    public static ResourceSnippet getFreeBoardRegisterCommentDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
               .description("자랑 게시판 댓글을 등록한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .requestFields(
                    fieldWithPath("boardId").type(SimpleType.NUMBER).description("게시판 아이디"),
                    fieldWithPath("content").type(SimpleType.STRING).description("댓글 내용"))
                .build());
    }

    public static ResourceSnippet getFreeBoardUpdateCommentDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .description("자랑 게시판 댓글을 수정한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .pathParameters(parameterWithName("id").description("자랑게시판 id"))
                .requestFields(
                    fieldWithPath("content").type(SimpleType.STRING).description("댓글 내용"))
                .build());
    }

    public static ResourceSnippet getFreeBoardDeleteCommentDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .description("자랑 게시판 댓글을 삭제한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .pathParameters(parameterWithName("id").description("자랑게시판 id"))
                .build());
    }
}
