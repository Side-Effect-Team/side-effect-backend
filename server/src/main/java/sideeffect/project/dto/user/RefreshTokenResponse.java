package sideeffect.project.dto.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sideeffect.project.domain.token.RefreshToken;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenResponse {

    private Long userId;
    private String refreshToken;

    public static RefreshTokenResponse of(RefreshToken refreshToken) {
        return RefreshTokenResponse.builder()
            .refreshToken(refreshToken.getRefreshToken())
            .userId(refreshToken.getUserId())
            .build();
    }
}
