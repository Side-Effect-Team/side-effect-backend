package sideeffect.project.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.user.ProviderType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinErrorResponse {

    private String email;
    private String imgUrl;
    private ProviderType providerType;

    public JoinErrorResponse(String email, String imgUrl, ProviderType providerType) {
        this.email = email;
        this.imgUrl = imgUrl;
        this.providerType = providerType;
    }
}
