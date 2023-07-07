package sideeffect.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.context.SecurityContextHolder;
import sideeffect.project.common.docs.ControllerTestDocument;
import sideeffect.project.common.docs.freeBoard.FreeBoardLikeDocsUtils;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResponse;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.security.UserDetailsImpl;
import sideeffect.project.service.LikeService;

@WebMvcTest(LikeController.class)
class LikeControllerTest extends ControllerTestDocument {

    @MockBean
    private LikeService likeService;

    @DisplayName("게시판을 추천한다.")
    @WithCustomUser
    @Test
    void like() throws Exception {
        User user = getUser();
        FreeBoard freeBoard = FreeBoard.builder().id(1L).build();
        Like like = Like.like(user, freeBoard);
        when(likeService.toggleLike(any(), any())).thenReturn(LikeResponse.of(like, LikeResult.LIKE));

        mvc.perform(RestDocumentationRequestBuilders.post("/api/like/{id}", 1L)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("upload", FreeBoardLikeDocsUtils.getFreeBoardLikeToggleDocs()));

        verify(likeService).toggleLike(any(), any());
    }

    private User getUser() {
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        return details.getUser();
    }
}
