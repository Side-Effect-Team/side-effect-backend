package sideeffect.project.common.docs.freeBoard;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippet;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import com.epages.restdocs.apispec.SimpleType;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

public final class FreeBoardDocsUtils {

    public static ResourceSnippet getFreeBoardFindDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .description("특정 게시판을 조회한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .pathParameters(parameterWithName("id").description("자랑게시판 id"))
                .responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("imgUrl").type(JsonFieldType.STRING).description("게시판 이미지"),
                    fieldWithPath("subTitle").type(JsonFieldType.STRING).description("게시판 한줄 요약"),
                    fieldWithPath("views").type(JsonFieldType.NUMBER).description("조회수"),
                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("작성자 id"),
                    fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일").optional(),
                    fieldWithPath("projectUrl").type(JsonFieldType.STRING).description("프로젝트 관련 url"),
                    fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 이름"),
                    fieldWithPath("like").type(JsonFieldType.BOOLEAN).description("좋아요 유무"),
                    fieldWithPath("likeNum").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 id"),
                    fieldWithPath("comments[].boardId").type(JsonFieldType.NUMBER).description("게시판 id"),
                    fieldWithPath("comments[].content").type(JsonFieldType.STRING).description("게시판 내용"),
                    fieldWithPath("comments[].writer").type(JsonFieldType.STRING).description("댓글 작성자 닉네임"),
                    fieldWithPath("comments[].writerId").type(JsonFieldType.NUMBER).description("댓글 작성자 id"))
                .build());
    }

    public static ResourceSnippet getFreeBoardScrollDocs() {
        return resource(
            ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .description("자랑 게시판을 스크롤 조회한다.")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .requestParameters(
                    parameterWithName("lastId").type(SimpleType.NUMBER).description("이전스크롤 마미작 게시판 Id").optional(),
                    parameterWithName("size").type(SimpleType.INTEGER).description("스크롤 게시판 개수").optional(),
                    parameterWithName("keyword").type(SimpleType.STRING).description("검색 키워드").optional(),
                    parameterWithName("filter").type(SimpleType.STRING)
                        .description("기준 정렬 (comment : 댓글순, latest : 최신순, like : 좋아요 순, views : 조회순,"
                            + " 입력이 없으면 최신순)").optional())
                .responseFields(
                    fieldWithPath("projects[].id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("projects[].imgUrl").type(JsonFieldType.STRING).description("게시판 이미지"),
                    fieldWithPath("projects[].subTitle").type(JsonFieldType.STRING).description("게시판 한줄 요약"),
                    fieldWithPath("projects[].views").type(JsonFieldType.NUMBER).description("조회수"),
                    fieldWithPath("projects[].title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("projects[].createdAt").type(JsonFieldType.STRING).description("생성일").optional(),
                    fieldWithPath("projects[].like").type(JsonFieldType.BOOLEAN).description("좋아요 유무"),
                    fieldWithPath("projects[].likeNum").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("projects[]..commentNum").type(JsonFieldType.NUMBER).description("댓글 개수"),
                    fieldWithPath("lastId").type(JsonFieldType.NUMBER).description("마지막 게시판 id"),
                    fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 게시판 유무"))
                .build());
    }

    public static ResourceSnippetParametersBuilder getFreeBoardRequestDocs() {
        return ResourceSnippetParameters.builder()
                .tag("자랑게시판 API")
                .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
                .requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("projectUrl").type(JsonFieldType.STRING).description("프로젝트 관련 url").optional(),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 이름").optional(),
                    fieldWithPath("subTitle").type(JsonFieldType.STRING).description("게시판 한줄 요약").optional());
    }

    public static ResourceSnippetParametersBuilder getFreeBoardPathParametersDocs() {
        return ResourceSnippetParameters.builder()
            .tag("자랑게시판 API")
            .requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰").optional())
            .pathParameters(parameterWithName("id").description("자랑게시판 id"));
    }
}
