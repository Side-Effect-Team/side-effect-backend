package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.recruit.BoardStack;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class UploadBoardResponse {

    private String category;
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private int commentNum;
    private String imgUrl;
    private List<StackType> stacks;

    public static List<UploadBoardResponse> listOf(User user) {
        List<UploadBoardResponse> uploadBoardResponseList = new ArrayList<>();
        List<FreeBoard> freeBoards = user.getFreeBoards();
        List<RecruitBoard> recruitBoards = user.getRecruitBoards();

        if(freeBoards!=null && !freeBoards.isEmpty()){
            uploadBoardResponseList.addAll(
                    freeBoards.stream()
                            .map(freeBoard -> getUploadBoardOfFree(freeBoard))
                            .collect(Collectors.toList())
            );
        }

        if(recruitBoards!=null && !recruitBoards.isEmpty()){
            uploadBoardResponseList.addAll(
                    recruitBoards.stream()
                            .map(recruitBoard -> getUploadBoardOfRecruit(recruitBoard))
                            .collect(Collectors.toList())
            );
        }

        return uploadBoardResponseList;
    }

    private static UploadBoardResponse getUploadBoardOfRecruit(RecruitBoard recruitBoard) {
        return UploadBoardResponse.builder()
                .category("recruits")
                .id(recruitBoard.getId())
                .title(recruitBoard.getTitle())
                .content(recruitBoard.getContents())
                .createAt(recruitBoard.getCreateAt())
                //.imgUrl(recruitBoard.getImgUrl())
                .stacks(getStackType(recruitBoard.getBoardStacks()))
                .build();
    }

    private static List<StackType> getStackType(List<BoardStack> boardStacks) {
        List<StackType> stackTypes = Collections.emptyList();
        if(boardStacks!=null && !boardStacks.isEmpty()){
            stackTypes = boardStacks.stream()
                    .map(boardStack -> boardStack.getStack().getStackType())
                    .collect(Collectors.toList());
        }
        return stackTypes;
    }

    private static UploadBoardResponse getUploadBoardOfFree(FreeBoard freeBoard) {
        return UploadBoardResponse.builder()
                .category("projects")
                .id(freeBoard.getId())
                .title(freeBoard.getTitle())
                .content(freeBoard.getContent())
                .createAt(freeBoard.getCreateAt())
                .commentNum(freeBoard.getComments().size())
                .imgUrl(freeBoard.getImgUrl())
                .build();
    }
}
