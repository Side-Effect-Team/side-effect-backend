package sideeffect.project.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseUserInfo {
    private String email;
    private String imgUrl;
}
