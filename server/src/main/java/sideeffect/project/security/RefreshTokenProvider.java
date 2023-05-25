package sideeffect.project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.token.RefreshToken;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.RefreshTokenResponse;
import sideeffect.project.redis.RefreshTokenRepository;
import sideeffect.project.repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenProvider {

    private static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 3;

    @Value("${jwt.secret}")
    private String secretKey;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String issueAccessToken(String refreshToken){
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        String email = userRepository.findEmailByUserId(token.getUserId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        return Jwts.builder()
                .setSubject(email)
                .claim("auth", UserRoleType.ROLE_USER)
                .setExpiration(createExpiration())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public RefreshTokenResponse createRefreshToken(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        RefreshToken refreshToken = generateRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        return RefreshTokenResponse.of(refreshToken);
    }

    private Date createExpiration() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
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
