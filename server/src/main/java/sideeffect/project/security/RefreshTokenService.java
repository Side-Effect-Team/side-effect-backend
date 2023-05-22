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
import sideeffect.project.repository.RefreshTokenRepository;
import sideeffect.project.repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String issueAccessToken(String refreshToken){
        RefreshToken findRefreshToken = refreshTokenRepository.findById(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        User findUser = userRepository.findById(findRefreshToken.getUserId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(findUser.getEmail())
                .claim("auth", UserRoleType.ROLE_USER)
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24 * 3))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String issueRefreshToken(Authentication authentication){
        UUID uuid = UUID.randomUUID();
        User user = (User) authentication.getPrincipal();
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(uuid.toString())
                .userId(user.getId())
                .build();
        refreshTokenRepository.save(refreshToken);
        return uuid.toString();
    }
}
