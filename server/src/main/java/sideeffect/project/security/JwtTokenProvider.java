package sideeffect.project.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.config.security.AuthProperties;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final int EXPIRATION_TIME = 1000 * 60 * 30;

    private final AuthProperties authProperties;
    private final UserDetailsServiceImpl userDetailsService;

    public boolean validateAccessToken(String accessToken){
        try {
            Jwts.parser().setSigningKey(authProperties.getSecret()).parseClaimsJws(accessToken);
            return false;
        } catch (UnsupportedJwtException e) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_MALFORMED);
        } catch (SignatureException e) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_SIGNATURE_FAILED);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_ILLEGAL_STATE);
        }
    }

    public String createAccessToken(Authentication authentication){
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        //access token
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(now + 1000 * 60 * 30))
                .signWith(SignatureAlgorithm.HS256, authProperties.getSecret())
                .compact();
    }

    public String createAccessToken(Long userId) {
        User user = getUser(userId);

        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("auth", UserRoleType.ROLE_USER)
            .claim("providerType", user.getProviderType())
            .setExpiration(createExpiration())
            .signWith(SignatureAlgorithm.HS256, authProperties.getSecret())
            .compact();
    }

    private User getUser(Long userId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUserId(userId);
        return userDetails.getUser();
    }

    public Authentication getAuthentication(String token){
        JwtTokenDto jwtTokenDto = decodeAccessToken(token);
        UserDetails userDetails = userDetailsService
            .loadUserByUsernameAndProviderType(jwtTokenDto.getUsername(), jwtTokenDto.getProviderType());

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    private Date createExpiration() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    private JwtTokenDto decodeAccessToken(String accessToken) {
        Claims claims = Jwts.parser().setSigningKey(authProperties.getSecret()).parseClaimsJws(accessToken).getBody();
        return new JwtTokenDto(claims.getSubject(), ProviderType.valueOf((String) claims.get("providerType")));
    }

    private static class JwtTokenDto {
        private final String username;
        private final ProviderType providerType;

        public JwtTokenDto(String username, ProviderType providerType) {
            this.username = username;
            this.providerType = providerType;
        }

        public String getUsername() {
            return username;
        }

        public ProviderType getProviderType() {
            return providerType;
        }
    }
}
