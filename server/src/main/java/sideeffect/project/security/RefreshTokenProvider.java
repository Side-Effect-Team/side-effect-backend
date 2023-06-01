package sideeffect.project.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.User;
import sideeffect.project.redis.RefreshTokenRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String issueAccessToken(String refreshToken){
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return jwtTokenProvider.createAccessToken(token.getUserId());
    }

    public RefreshToken createRefreshToken(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        RefreshToken refreshToken = generateRefreshToken(user);
        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser();
    }

    private RefreshToken generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        return RefreshToken.builder()
            .refreshToken(token)
            .userId(user.getId())
            .build();
    }
}
